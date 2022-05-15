package de.amirrocker.speechtonavigation.classifier.sound

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.amirrocker.speechtonavigation.R
import de.amirrocker.speechtonavigation.ui.theme.gray800
import de.amirrocker.speechtonavigation.ui.theme.resultItemColorPairs
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
    probabilities: List<Category>,
    classifierEnabled: Boolean,
    classificationInterval: Long,
    onStandByToggle: (Boolean) -> Unit,
    onIntervalChanged: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        ControlPanel(
            inputEnabled = classifierEnabled,
            onIntervalChanged = onIntervalChanged,
            interval = classificationInterval,
            onInputChanged = onStandByToggle,
        )

        Divider(modifier = Modifier.padding(24.dp))

        if (classifierEnabled) {
            probabilities.let { itemList ->
                LazyColumn {
                    itemsIndexed(
                        items = itemList,
                        key = { _, item -> item.label }
                    ) { index, item ->
                        ClassificationResultItem(
                            text = item.label,
                            progress = item.score,
                            index = index
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ControlPanel(
    inputEnabled: Boolean,
    interval: Long,
    onInputChanged: (Boolean) -> Unit = {},
    onIntervalChanged: (Long) -> Unit = {},
) {
    Column {

        Row(verticalAlignment = Alignment.CenterVertically) {
            val onStandbySwitchLabel = stringResource(id = R.string.on_standby_switch_label)
            Text(text = onStandbySwitchLabel)
            Switch(
                checked = inputEnabled,
                onCheckedChange = onInputChanged,
                modifier = Modifier.padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            val intervalLabel = stringResource(id = R.string.interval_label)
            Text(text = intervalLabel)
            Slider(
                value = interval / 1000f, onValueChange = {
                    onIntervalChanged((it * 1000L).toLong())
                }, modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = gray800,
                    activeTrackColor = gray800
                )
            )
        }
    }
}

@Composable
fun ClassificationResultItem(text: String, progress: Float, index: Int = 0) {

    val indicatorColor = resultItemColorPairs[index % 3].second
    val backgroundColor = resultItemColorPairs[index % 3].first

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.width(92.dp),
            style = MaterialTheme.typography.body2
        )
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .height(52.dp)
                .padding(all = 16.dp)
                .clip(MaterialTheme.shapes.medium)
                .fillMaxWidth(),
            color = indicatorColor,
            backgroundColor = backgroundColor
        )
    }
}


@Preview
@Composable
fun SoundClassifierViewPreview() {
    val viewModel: SoundClassifierViewModel = SoundClassifierViewModel(Application())
    SoundClassifierView(viewModel)
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