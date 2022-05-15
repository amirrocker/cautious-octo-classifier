package de.amirrocker.speechtonavigation

import de.amirrocker.speechtonavigation.home.HomeViewModel
import de.amirrocker.speechtonavigation.home.NoteVO
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class HomeViewModelTest {

    @Test
    fun `given fab when pushed then expect a new note to be created`() {

        val viewModel = HomeViewModel()
        val note = NoteVO("New Note", "Body", "footnote")

        viewModel.addNewNote("Body")

//        assertEquals("Expect Header but was ${viewModel.uiState.value.notes.first().header}", note.header, viewModel.notes.first().header )

        val notes = viewModel.uiState.value.notes
        val firstNote = notes.first()

        notes.size shouldBe 1

        firstNote.header shouldBeEqualTo note.header
        firstNote.body shouldBeEqualTo note.body

    }
}