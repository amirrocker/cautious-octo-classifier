package de.amirrocker.speechtonavigation.home

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class HomeViewState(
    val header: String = "Header",
    var notes: List<NoteVO> = emptyList()
)

class HomeViewModel() : ViewModel() {

    var uiState: MutableState<HomeViewState> = mutableStateOf(HomeViewState())

    val isListening = mutableStateOf(false)

    val listener = object : RecognitionListener {
        override fun onReadyForSpeech(p0: Bundle?) {
            println("onReadyForSpeech : $p0")
//            listenButtonHeader = "Ready to listen .... "
        }

        override fun onBeginningOfSpeech() {
            println("onBeginningOfSpeech")
//            listenButtonHeader = "listening .... "
        }

        override fun onRmsChanged(p0: Float) {
            println("onRmsChanged: $p0")
        }

        override fun onBufferReceived(p0: ByteArray?) {
            println("onBufferReceived : $p0")
        }

        override fun onEndOfSpeech() {
            println("onEndOfSpeech")
        }

        override fun onError(p0: Int) {
            println("onError : $p0")
        }

        override fun onResults(results: Bundle?) {
            println("onResults: $results")
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val scores = results?.getStringArray(SpeechRecognizer.CONFIDENCE_SCORES)
            println("matches: $matches")
            println("scores: $scores")

            addNewNote(
                matches?.firstOrNull()
                    ?: "Could not recognize any Text. Did you really speak? loud enough?"
            )
        }

        override fun onPartialResults(p0: Bundle?) {
            println("onPartialResults: $p0")
        }

        override fun onEvent(p0: Int, p1: Bundle?) {
            println("onEvent: $p0")
        }
    }

    fun addNewNote(result: String) {

        val notes = uiState.value.notes
        val note = NoteVO(
            "", "Result: $result", "Recorded at: ${
                LocalDate.now(ZoneId.systemDefault()).format(
                    DateTimeFormatter.ISO_DATE
                )
            }"
        )
        val newNotes = mutableListOf(*notes.toTypedArray())
        newNotes.add(note) // .slice(IntRange(0, notes.size))
        uiState.value = HomeViewState(notes = newNotes, header = "New Header")

    }

    fun stopListening() = isListening.apply { value = false }
    fun startListening() = isListening.apply { value = true }

}