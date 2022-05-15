package de.amirrocker.speechtonavigation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import de.amirrocker.speechtonavigation.Constants.RECORDING_LENGTH
import de.amirrocker.speechtonavigation.classifier.localmodel.LocalModelClassifierView
import de.amirrocker.speechtonavigation.classifier.localmodel.LocalModelClassifierViewModel
import de.amirrocker.speechtonavigation.classifier.sound.SoundClassifierViewModel
import de.amirrocker.speechtonavigation.home.HomeView
import de.amirrocker.speechtonavigation.ui.theme.SpeechToNavigationTheme
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class MainActivity : ComponentActivity() {
    // no koin inserted yet
//    private val viewModel by inject()
    private val viewModel: SoundClassifierViewModel by viewModels()
    private val localModelClassifierViewModel: LocalModelClassifierViewModel by viewModels()

    fun setupSpeechRecognition(context: Context, listener: RecognitionListener): SpeechRecognizer {
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

        speechRecognizer.setRecognitionListener(listener)

        return speechRecognizer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkPermission()
        }
        setContent {
            SpeechToNavigationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

//                    HomeView()

                    // LocalModelClassifier
                    loadTfLiteModel()

                    LocalModelClassifierView(localModelClassifierViewModel)


                    // soundclassifier currently in development

//                    SoundClassifierView(viewModel)
//
//                    // when resumed ?
//                    lifecycleScope.launchWhenCreated {
//                        viewModel.classifierEnabled.collectLatest { isEnabled ->
//                            if(isEnabled) {
//                                viewModel.startSoundClassification()
//                            } else {
//                                viewModel.stopSoundClassification()
//                            }
//                            keepScreenOn(isEnabled)
//                        }
//                    }
                    // uncomment up to here ...
                }
            }
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1234)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1234 && grantResults.size > 0) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show()
        }
    }

    private fun keepScreenOn(enable: Boolean = true) =
        if (enable) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

    fun loadTfLiteModel() {


    }

    val LABEL_FILENAME = "file:///android_asset/conv_actions_labels.txt"
    val MODEL_FILENAME = "file:///android_asset/conv_actions_labels.txt"

    val labels: MutableList<String> = mutableListOf()
    val displayedLabels: MutableList<String> = mutableListOf()

    val tfLiteModel: ByteBuffer? = null
    val tfLiteOptions: Interpreter.Options = Interpreter.Options()


    fun loadModelFile(assetManager: AssetManager, modelFileName: String): MappedByteBuffer {

        val fileDescriptor: AssetFileDescriptor = assetManager.openFd(modelFileName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel

        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength

        val result = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        println("result: $result")
        return result
    }


    fun loadLabelFile(assetManager: AssetManager, labelFileName: String): List<String> {

        val actualFileName = LABEL_FILENAME.split("file:///android_asset/")
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()[1]

        println("reading labels from : $actualFileName")

        try {
            val bufferedReader =
                BufferedReader(InputStreamReader(assetManager.open(actualFileName)))
            var line: String? = ""
            line = bufferedReader.readLine()
            while ((line) != null) {
                line = bufferedReader.readLine()
                labels.add(line)
                if (!line.first().equals("_")) {
                    displayedLabels.add(line.substring(0, 1).uppercase() + line.substring(1))
                }
            }
            bufferedReader.close()
        } catch (ex: IOException) {
            throw RuntimeException("Problem reading label fileaas")
        }
        return emptyList()
    }

    private var tfLite: Interpreter? = null
    private val tfLiteLock: Lock = ReentrantLock()

    fun recreateInterpreter() {

        tfLiteLock.lock()
        try {
            if (tfLite != null) {
                tfLite?.close()
                tfLite = null
            }
            tfLite = tfLiteModel?.let { Interpreter(it, tfLiteOptions) }.also {
                it?.resizeInput(0, arrayOf(RECORDING_LENGTH, 1).toIntArray())
                it?.resizeInput(1, arrayOf(1).toIntArray())
            }
        } finally {
            tfLiteLock.unlock()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SpeechToNavigationTheme {
        HomeView()
    }
}