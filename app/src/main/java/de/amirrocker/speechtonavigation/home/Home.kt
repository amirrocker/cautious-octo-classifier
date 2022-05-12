package de.amirrocker.speechtonavigation.home

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.amirrocker.speechtonavigation.R
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

data class NoteVO(
    val header: String,
    val body: String,
    val footNote: String,
    val creationDate: Long = LocalDateTime.now(ZoneId.systemDefault()).toInstant(ZoneOffset.UTC)
        .toEpochMilli()
)

val comparator = Comparator { noteVOA: NoteVO, noteVOB: NoteVO ->
    if (noteVOA.creationDate > noteVOB.creationDate) {
        return@Comparator 1
    } else
        if (noteVOA.creationDate < noteVOB.creationDate) {
            return@Comparator -1
        } else {
            return@Comparator 0
        }
}

fun setupSpeechRecognition(context: Context, listener: RecognitionListener): SpeechRecognizer {
    val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    speechRecognizer.setRecognitionListener(listener)

    return speechRecognizer
}


@Composable
fun HomeView(viewModel: HomeViewModel = HomeViewModel()) {

    val uiState = remember {
        viewModel.uiState
    }

    val speechRecognizer = setupSpeechRecognition(
        context = LocalContext.current,
        listener = viewModel.listener
    )

    val isListening by remember { viewModel.isListening }

    Scaffold(
        drawerContent = {},
        topBar = {},
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    println("pressed FAB ${viewModel.uiState.value.notes.size} times")
                    if (!isListening) {
                        println("start listening clicked")
                        viewModel.startListening()
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                        intent.putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                        )
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

                        speechRecognizer.startListening(intent)
                    } else {
                        viewModel.stopListening()
                        speechRecognizer.stopListening()
                    }
                },
                modifier = Modifier
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    modifier = Modifier.size(20.dp, 20.dp),
                    contentDescription = ""
                )
            }
        }
    )
    {
        Home(uiState.value.notes)
    }
}

@Composable
fun Home(noteVOS: List<NoteVO>) {
    Column {

        // Notes header
        HeaderArea()
        // NotesList
        NotesList(header = "Notes List Header: ", noteVOS = noteVOS,
            comparator,
            onNoteClicked = {
                println("clicked note : $it")
            }
        )

        // switch on and off
        OnStandBySwitch(value = false, onValueChanged = {
            println("changed switch to $it")
        })
    }
}

@Composable
fun HeaderArea() {
    Row {
        Text(text = "Notes: ")
    }
}


@Composable
fun NotesList(
    header: String,
    noteVOS: List<NoteVO>,
    sortComparator: Comparator<NoteVO>,
    onNoteClicked: (String) -> Unit
) {

    val sortedNotes = remember(noteVOS, sortComparator) {
        noteVOS.sortedWith(sortComparator)
    }

    Column {
        Text(text = header, style = MaterialTheme.typography.caption)
        Divider()

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            items(sortedNotes) { note ->
                Note(note)
                Divider()
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Note(noteVO: NoteVO) {

    val isExpanded by remember { mutableStateOf(false) }
    val color = animateColorAsState(if (isExpanded) Color.Blue else Color.Green)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray),
        onClick = {
            println("Note clicked .... ")
        }) {
        Column(modifier = Modifier) {
            Row(Modifier.background(Color.Cyan)) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    modifier = Modifier.size(20.dp, 20.dp),
                    contentDescription = "Some Description text"
                )
                Text(text = noteVO.header, modifier = Modifier.fillMaxWidth())
            }
            Column(Modifier.background(Color.Green)) {
                Text(text = noteVO.body, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = noteVO.footNote, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}


@Composable
fun OnStandBySwitch(
    value: Boolean,
    onValueChanged: (Boolean) -> Unit
) {
    Row {
        Text(text = "On Standby: ")
        Switch(checked = value, onCheckedChange = onValueChanged)
    }
}


@Preview
@Composable
fun HomePreview() {
    Home(mockNotes())
}

@Preview
@Composable
fun NotePreview() {
    Note(mockNotes().first())
}


fun mockNotes() = listOf(
    NoteVO("Header 1", "Body 1", "Footnote 1"),
    NoteVO("Header 2", "Body 2", "Footnote 2"),
    NoteVO("Header 3", "Body 3", "Footnote 3"),
    NoteVO("Note 4", "Body 4", "Footnote 4"),
    NoteVO("Note 5", "Body 5", "Footnote 5"),
    NoteVO("Note 6", "Body 6", "Footnote 6"),
    NoteVO("Note 7", "Body 7", "Footnote 7"),
)




