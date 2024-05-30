package com.fluxtah.ask.app.audio

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.LineUnavailableException
import javax.sound.sampled.TargetDataLine

class AudioRecorder {
    private var line: TargetDataLine? = null
    private val fileType = AudioFileFormat.Type.WAVE
    private val wavFile = File("RecordAudio.wav")

    fun getAudioFile(): File = wavFile
    suspend fun start() = withContext(Dispatchers.IO) {
        try {
            val format = getAudioFormat()
            val info = DataLine.Info(TargetDataLine::class.java, format)

            if (!AudioSystem.isLineSupported(info)) {
                println("Line not supported")
                return@withContext
            }

            line = AudioSystem.getLine(info) as TargetDataLine
            line!!.open(format)
            line!!.start()

            val ais = AudioInputStream(line)
            AudioSystem.write(ais, fileType, wavFile)
        } catch (ex: LineUnavailableException) {
            ex.printStackTrace()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    fun stop() {
        line?.stop()
        line?.close()
        line = null
    }

    fun isRecording(): Boolean {
        return line != null
    }

    private fun getAudioFormat(): AudioFormat {
        val sampleRate = 16000f
        val sampleSizeInBits = 16
        val channels = 1
        val signed = true
        val bigEndian = true
        return AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian)
    }

    suspend fun startStop() {
        if (line == null) {
            start()
        } else {
            stop()
        }
    }
}