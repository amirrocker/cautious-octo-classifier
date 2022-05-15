package de.amirrocker.speechtonavigation.classifier.sound

import android.app.Application
import android.media.AudioRecord
import android.os.Handler
import android.os.HandlerThread
import androidx.core.os.HandlerCompat
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.audio.classifier.AudioClassifier

class SoundClassifierViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val MODEL_FILE = "yamnet.tflite"
    private val MIN_DISPLAY_THRESHOLD = 0.3f

    private val _probabilities: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())
    val probabilities = _probabilities.asStateFlow()

    private val _classiferEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val classifierEnabled = _classiferEnabled.asStateFlow()

    private val _classificationInterval: MutableStateFlow<Long> = MutableStateFlow(0L)
    val classificationInterval = _classificationInterval.asStateFlow()

    private var audioClassifier: AudioClassifier? = null
    private var audioRecord: AudioRecord? = null

    // create two versions - one with threads, another with coroutines
    // each version runs classification in the background
    private var handler: Handler

    init {
        val handlerThread = HandlerThread("backgroundThread")
        handlerThread.start()
        handler = HandlerCompat.createAsync(handlerThread.looper)
    }

    fun startSoundClassification() {
        if (audioClassifier != null) {
            enableClassifier()
            return
        }

        val classifier = AudioClassifier.createFromFile(getApplication(), MODEL_FILE)
        val audioTensor = classifier.createInputTensorAudio()

        val record = classifier.createAudioRecord()
        record.startRecording()

        // classification
        // 1. Runnable
        val run = object : Runnable {
            override fun run() {
                val startTime = System.currentTimeMillis()

                audioTensor.load(record)
                val output = classifier.classify(audioTensor)

                val processed = output[0].categories.filter { category ->
                    category.score > MIN_DISPLAY_THRESHOLD
                }.sortedBy { category ->
                    -category.score
                }
                val finishedTime = System.currentTimeMillis()
                println("elapsed Time (latency): ${finishedTime - startTime} ms")

                setProbabilities(processed)

                handler.postDelayed(this, classificationInterval.value)
            }
        }

        // start classification
        handler.post(run)

        audioClassifier = classifier
        audioRecord = record

    }

    fun stopSoundClassification() {
        handler.removeCallbacksAndMessages(null)
        audioRecord?.stop()
        audioRecord = null
        audioClassifier = null

    }


    fun setProbabilities(processedOutput: List<Category>) {
        println("processedOutput: $processedOutput")
        _probabilities.value = processedOutput
    }


    fun enableClassifier() = _classiferEnabled.apply { value = true }

    fun disableClassifier() = _classiferEnabled.apply { value = false }

    fun setClassificationInterval(interval: Long) =
        _classificationInterval.apply { value = interval }

    fun setClassificationEnabled(enabled: Boolean) = _classiferEnabled.apply { value = enabled }


}