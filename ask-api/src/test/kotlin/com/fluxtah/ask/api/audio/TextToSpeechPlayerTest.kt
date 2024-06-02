package com.fluxtah.ask.api.audio

import AudioPlayer
import com.fluxtah.ask.api.audio.TextToSpeechPlayer.TtsSegment
import com.fluxtah.ask.api.clients.openai.audio.AudioApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import kotlin.test.Test

@ExperimentalCoroutinesApi
class TextToSpeechPlayerTest {

    private val audioApi: AudioApi = mockk(relaxed = true)
    private val audioPlayer: AudioPlayer = mockk(relaxed = true)

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val textToSpeechPlayer = TextToSpeechPlayer(audioApi, audioPlayer, coroutineScope)

    @Test
    fun `queue should add text segments`() = runTest {
        val text = "This is a test text."
        textToSpeechPlayer.queue(text)

        assertEquals(1, textToSpeechPlayer.ttsSegments.size)
        assertTrue(textToSpeechPlayer.ttsSegments.first() is TtsSegment.Text)
        assertTrue((textToSpeechPlayer.ttsSegments.first() as TtsSegment.Text).autoPlay)
    }

    @Test
    fun `queue should add code block segments`() = runTest {
        val text ="""
            This is a test text.
            
            ```kotlin
            println('Hello World!')
            ```
            
            This is more text.
            
        """.trimIndent()

        textToSpeechPlayer.queue(text)

        assertEquals(3, textToSpeechPlayer.ttsSegments.size)
        assertTrue(textToSpeechPlayer.ttsSegments[0] is TtsSegment.Text)
        assertTrue(textToSpeechPlayer.ttsSegments[1] is TtsSegment.CodeBlock)
        assertTrue(textToSpeechPlayer.ttsSegments[2] is TtsSegment.Text)
    }

    @Test
    fun `text after code block segments should autoplay`() = runTest {
        val text ="""
            This is a test text.
            
            ```kotlin
            println('Hello World!')
            ```
            
            This is more text.
            
        """.trimIndent()

        textToSpeechPlayer.queue(text)

        assertEquals(3, textToSpeechPlayer.ttsSegments.size)
        assertTrue((textToSpeechPlayer.ttsSegments[2] as TtsSegment.Text).autoPlay)
    }

    @Test
    fun `skipNext should skip the next segment`() = runTest {
        val text ="""
            This is a test text.
            
            ```kotlin
            println('Hello World!')
            ```
            
            This is more text.
            
        """.trimIndent()

        textToSpeechPlayer.queue(text)

        assertEquals(3, textToSpeechPlayer.ttsSegments.size)

        textToSpeechPlayer.skipNext()

        assertEquals(2, textToSpeechPlayer.ttsSegments.size)
    }

    @Test
    fun `text before code block segments should end with play or skip advice`() = runTest {
        val expected = "This is a test text.\n\n$ADVICE_PLAY_OR_SKIP_CODE"
        val text ="""
            This is a test text.
            
            ```kotlin
            println('Hello World!')
            ```
            
            This is more text.
            
        """.trimIndent()

        textToSpeechPlayer.queue(text)

        assertEquals(3, textToSpeechPlayer.ttsSegments.size)
        assertEquals(expected, (textToSpeechPlayer.ttsSegments[0] as TtsSegment.Text).content)

    }

    @Test
    fun `playNext should play the next segment`() = runTest {
        coEvery { audioApi.createSpeech(any()) } returns byteArrayOf(1, 2, 3)
        val text = "This is a test text."
        textToSpeechPlayer.queue(text)
        textToSpeechPlayer.playNext()

        coVerify { audioPlayer.play(any(), any()) }
    }

    @Test
    fun `playNext should queue autoPlay for next text segment`() = runTest {
        coEvery { audioApi.createSpeech(any()) } returns byteArrayOf(1, 2, 3)
        val text = "This is a test text."
        val code = "println('Hello World!')"
        textToSpeechPlayer.queue(text)
        textToSpeechPlayer.queue(code)
        textToSpeechPlayer.playNext()

        coVerify { audioPlayer.play(any(), any()) }
        assertTrue(textToSpeechPlayer.ttsSegments.first().autoPlay)
    }

    @Test
    fun `stop should stop the audio player`() = runTest {
        textToSpeechPlayer.stop()

        verify { audioPlayer.stop() }
    }

    @Test
    fun `clear should clear the segments`() = runTest {
        val text = "This is a test text."
        textToSpeechPlayer.queue(text)
        textToSpeechPlayer.clear()

        assertTrue(textToSpeechPlayer.ttsSegments.isEmpty())
    }
}
