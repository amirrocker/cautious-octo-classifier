package de.amirrocker.speechtonavigation.classifier.localmodel

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import de.amirrocker.speechtonavigation.Constants.RECORDING_LENGTH
import de.amirrocker.speechtonavigation.Constants.SAMPLE_RATE
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.max

object AudioRecorder {

    // TODO move to Constants file
//    val SAMPLE_RATE = 16000
//    val SAMPLE_DURATION = 1000
//    val RECORDING_LENGTH = (SAMPLE_RATE * SAMPLE_DURATION / 1000)

    private var shouldContinueListening = true
    private val recordingBuffer: Array<Short> = Array(RECORDING_LENGTH) { 0 }
    private var recordingOffset: Int = 0

    private val recordingBufferLock: Lock = ReentrantLock()

    @SuppressLint("MissingPermission")
    fun record() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO)

        var bufferSize = estimateBufferSize()
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2
        }
        val audioBuffer: Array<Short> = Array(bufferSize / 2) { 0 }

        val record = AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        if (record.state != AudioRecord.STATE_INITIALIZED) {
            error("Audio cant be initialized")
        }

        record.startRecording()
        println("starting recording. shouldContinueListening $shouldContinueListening")

        while (shouldContinueListening) {
            val numberRead = record.read(audioBuffer.toShortArray(), 0, audioBuffer.size)
            val maxLength = recordingBuffer.size
            val newRecordingOffset = recordingOffset + numberRead
            val secondCopyLength = max(0, newRecordingOffset - maxLength)
            val firstCopyLength = numberRead - secondCopyLength

            println("numberRead: $numberRead")
            println("maxLength: $maxLength")
            println("newRecordingOffset: $newRecordingOffset")
            println("secondCopyLength: $secondCopyLength")
            println("firstCopyLength: $firstCopyLength")

            println("lock .... ")

            recordingBufferLock.lock()

            try {
                System.arraycopy(audioBuffer, 0, recordingBuffer, recordingOffset, firstCopyLength)
                System.arraycopy(audioBuffer, firstCopyLength, recordingBuffer, 0, secondCopyLength)
                recordingOffset = newRecordingOffset % maxLength
                println("copied buffers .... ")
                println("new recordingOffset: $recordingOffset")
            } finally {
                println("unlock .... ")
                recordingBufferLock.unlock()
            }
        }

        record.stop()
        record.release()
    }

    private fun estimateBufferSize() =
        AudioRecord.getMinBufferSize(
            SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
        )


}