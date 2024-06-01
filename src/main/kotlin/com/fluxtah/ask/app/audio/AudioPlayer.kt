import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine

class AudioPlayer {

    private var line: SourceDataLine? = null

    suspend fun play(audioData: ByteArray, onComplete: () -> Unit = {}) = withContext(Dispatchers.IO) {
        if (line != null) {
            stop()
        }
        try {
            val bais = ByteArrayInputStream(audioData)
            val audioStream: AudioInputStream = AudioSystem.getAudioInputStream(bais)
            val format = audioStream.format
            val info = DataLine.Info(SourceDataLine::class.java, format)

            if (!AudioSystem.isLineSupported(info)) {
                println("Line not supported")
                return@withContext
            }

            line = AudioSystem.getLine(info) as SourceDataLine
            line?.open(format)
            line?.start()

            // Gradually ramp up the volume at the start
            val buffer = ByteArray(4096)
            var bytesRead = audioStream.read(buffer, 0, buffer.size)
            while (bytesRead != -1) {
                line?.write(buffer, 0, bytesRead)
                if (line == null) {
                    break
                }
                bytesRead = audioStream.read(buffer, 0, buffer.size)
            }

            line?.drain()
            line?.close()
            line = null
            onComplete()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun stop() {
        line?.stop()
        line?.close()
        line = null
    }

    fun isPlaying(): Boolean {
        return line != null
    }
}
