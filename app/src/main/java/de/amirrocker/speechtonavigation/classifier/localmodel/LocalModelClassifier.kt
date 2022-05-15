package de.amirrocker.speechtonavigation.classifier.localmodel

import androidx.compose.runtime.Composable

@Composable
fun LocalModelClassifierView(
    viewModel: LocalModelClassifierViewModel
) {

    viewModel.listenForInput()

}


