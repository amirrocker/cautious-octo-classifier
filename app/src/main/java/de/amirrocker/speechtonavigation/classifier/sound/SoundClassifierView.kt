package de.amirrocker.speechtonavigation.classifier.sound

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.amirrocker.speechtonavigation.R
import org.tensorflow.lite.support.label.Category

@Composable
fun SoundClassifierView(
    viewModel: SoundClassifierViewModel
) {

    val classifierEnabled by viewModel.classifierEnabled.collectAsState()
    val classificationInterval by viewModel.classificationInterval.collectAsState()
    val probabilities by viewModel.probabilities.collectAsState()

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            modifier = Modifier,
            topBar = {
                TopAppBar(
                    title = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "",
                            modifier = Modifier.sizeIn(maxWidth = 180.dp)
                        )
                    },
                )
            },
            backgroundColor = MaterialTheme.colors.primarySurface
        ) { innerPaddingValues ->
            SoundClassifierView(
                probabilities = probabilities,
                classifierEnabled = classifierEnabled,
                classificationInterval = classificationInterval,
                onStandByToggle = viewModel::setClassificationEnabled,
                onIntervalChanged = viewModel::setClassificationInterval,
                modifier = Modifier.padding(innerPaddingValues)
            )
        }
    }
}

@Composable
fun SoundClassifierView(
    probabilities: List<Category?>,
    classifierEnabled: Boolean,
    classificationInterval: Long,
    onStandByToggle: (Boolean) -> Unit,
    onIntervalChanged: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
//        ControlPanel()
    }
}

@Composable
fun ControlPanel(
    inputEnabled: Boolean,
    interval: Long,
    onInputChanged: (Boolean) -> Unit = {},
    onIntervalChanged: (Long) -> Unit = {},
) {
    Row {
        val onStandbySwitchLabel = stringResource(id = R.string.on_standby_switch_label)

    }
}


@Preview
@Composable
fun ControlPanelPreview() {
    ControlPanel(
        inputEnabled = true,
        interval = 1L,
        onInputChanged = {
            println("")
        },
        onIntervalChanged = {
            println("onInterval ")
        },
    )
}