package de.amirrocker.speechtonavigation.classifier.localmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * The lifecycle of a model could look like this:
 *
 * 1. Every time a audio classification has been requested the recorded audio block will be transmitted
 * to the server and stored in a separate datasource.
 * 2. Each night the server checks whether enough 'recordings' have been assembled and if so processes
 * the data and prepares it for training the model.
 * 3. Once the data has been prepped, training commences and once it reaches a certain accuracy, the
 * model will then be tested and validated.
 * 4. The newly trained model, if it meets all Q&A criteria, will be pushed to all clients.
 * 5. The client receives the new model and further on uses that model for classification or
 * prediction.
 *
 * this lifecycle would over time build a solid training library to train models with.
 *
 */
class LocalModelClassifierViewModel(
    application: Application
) : AndroidViewModel(application) {

    val _audioInput: MutableStateFlow<Map<String, String>> = MutableStateFlow(emptyMap())
    val audioInput = _audioInput.asStateFlow()

    fun listenForInput() {

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                AudioRecorder.record()
            }
        }


        // final result
        _audioInput.apply { this.value = mapOf() }
    }

//    // TODO move to Constants file
//    val SAMPLE_RATE = 16000
//    val SAMPLE_DURATION = 1000
//    val RECORDING_LENGTH = (SAMPLE_RATE * SAMPLE_DURATION/1000)
//
//    private var shouldContinueListening = true
//    private val recordingBuffer: Array<Short> = Array(RECORDING_LENGTH) {0}
//    private var recordingOffset: Int = 0

//    private val recordingBufferLock : Lock = ReentrantLock()

//    @SuppressLint("MissingPermission")
//    fun record() {
//        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO)
//
//        var bufferSize = estimateBufferSize()
//        if(bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
//            bufferSize = SAMPLE_RATE * 2
//        }
//        val audioBuffer: Array<Short> = Array(bufferSize/2) { 0 }
//
//        val record = AudioRecord(
//            MediaRecorder.AudioSource.DEFAULT,
//            SAMPLE_RATE,
//            AudioFormat.CHANNEL_IN_MONO,
//            AudioFormat.ENCODING_PCM_16BIT,
//            bufferSize
//        )
//
//        if(record.state != AudioRecord.STATE_INITIALIZED) {
//            error("Audio cant be initialized")
//        }
//
//        record.startRecording()
//        println("starting recording. shouldContinueListening $shouldContinueListening")
//
//        while (shouldContinueListening) {
//            val numberRead = record.read(audioBuffer.toShortArray(), 0, audioBuffer.size)
//            val maxLength = recordingBuffer.size
//            val newRecordingOffset = recordingOffset + numberRead
//            val secondCopyLength = max(0, newRecordingOffset - maxLength)
//            val firstCopyLength = numberRead - secondCopyLength
//
//            println("numberRead: $numberRead")
//            println("maxLength: $maxLength")
//            println("newRecordingOffset: $newRecordingOffset")
//            println("secondCopyLength: $secondCopyLength")
//            println("firstCopyLength: $firstCopyLength")
//
//            println("lock .... ")
//
//            recordingBufferLock.lock()
//
//            try {
//                System.arraycopy(audioBuffer, 0, recordingBuffer, recordingOffset, firstCopyLength)
//                System.arraycopy(audioBuffer, firstCopyLength, recordingBuffer, 0, secondCopyLength)
//                recordingOffset = newRecordingOffset % maxLength
//                println("copied buffers .... ")
//                println("new recordingOffset: $recordingOffset")
//            } finally {
//                println("unlock .... ")
//                recordingBufferLock.unlock()
//            }
//        }
//
//        record.stop()
//        record.release()
//    }


//    private fun estimateBufferSize() =
//        AudioRecord.getMinBufferSize(
//        SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
//    )


}