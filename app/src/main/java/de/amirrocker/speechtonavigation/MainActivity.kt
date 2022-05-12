package de.amirrocker.speechtonavigation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
import de.amirrocker.speechtonavigation.classifier.sound.SoundClassifierViewModel
import de.amirrocker.speechtonavigation.home.HomeView
import de.amirrocker.speechtonavigation.ui.theme.SpeechToNavigationTheme

class MainActivity : ComponentActivity() {
    // no koin inserted yet
//    private val viewModel by inject()
    private val viewModel: SoundClassifierViewModel by viewModels()

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
                    HomeView()

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
}

//@OptIn(ExperimentalAnimationApi::class)
//@Composable
//fun Greeting(name: String, context: Context) {
//
//
//    var listenButtonHeader by remember { mutableStateOf("click me here ... ") }
//    var rememberedText by remember { mutableStateOf("Nothing recognized.") }
//    var isListening by remember { mutableStateOf(false) }
//    var expanded by remember {
//        mutableStateOf(false)
//    }
//
//    fun setupSpeechRecognition(listener: RecognitionListener):SpeechRecognizer {
//        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
//
//        speechRecognizer.setRecognitionListener(listener)
//
//        return speechRecognizer
//    }
//
//    val listener = object : RecognitionListener {
//        override fun onReadyForSpeech(p0: Bundle?) {
//            println("onReadyForSpeech : $p0")
//            listenButtonHeader = "Ready to listen .... "
//        }
//
//        override fun onBeginningOfSpeech() {
//            println("onBeginningOfSpeech")
//            listenButtonHeader = "listening .... "
//        }
//
//        override fun onRmsChanged(p0: Float) {
//            println("onRmsChanged: $p0")
//        }
//
//        override fun onBufferReceived(p0: ByteArray?) {
//            println("onBufferReceived : $p0")
//        }
//
//        override fun onEndOfSpeech() {
//            println("onEndOfSpeech")
//        }
//
//        override fun onError(p0: Int) {
//            println("onError : $p0")
//        }
//
//        override fun onResults(results: Bundle?) {
//            println("onResults: $results")
//            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
//            val scores = results?.getStringArray(SpeechRecognizer.CONFIDENCE_SCORES)
//            println("matches: $matches")
//            println("scores: $scores")
//
//            listenButtonHeader = "click me here ...."
//            rememberedText = "What I Heard: $matches"
//        }
//
//        override fun onPartialResults(p0: Bundle?) {
//            println("onPartialResults: $p0")
//        }
//
//        override fun onEvent(p0: Int, p1: Bundle?) {
//            println("onEvent: $p0")
//        }
//    }
//
//    val speechRecognizer = setupSpeechRecognition(listener = listener)
//
//    Column(modifier = Modifier.fillMaxSize()) {
//
//        Button(onClick = {
//            if(!isListening) {
//                println("start listening clicked")
//
//                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//                intent.putExtra(
//                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
//                )
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
//
//                speechRecognizer.startListening(intent)
//            } else {
//                speechRecognizer.stopListening()
//                isListening = false
//            }
//
//
//        }) {
//            Column(modifier = Modifier.fillMaxWidth()) {
//                Text(listenButtonHeader)
//            }
//        }
//        Spacer(modifier = Modifier.size(height = 16.dp, width = 0.dp))
//        Column(Modifier.clickable { expanded = !expanded }) {
//            AnimatedVisibility(visible = expanded) {
//                Text(rememberedText)
//            }
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SpeechToNavigationTheme {
        //Greeting("Android", LocalContext.current )
        HomeView()
    }
}