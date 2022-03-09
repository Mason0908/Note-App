package com.example.noteapp

import kotlin.test.Test
import kotlin.test.assertEquals

internal class FolderNoteTest {

    private var noteBoard = mutableListOf<Note>()
    private var folders = mutableListOf<Folder>()

    @Test
    fun initial_zero() {
        val expected = 0
        assertEquals(expected, noteBoard.size)
    }

    @Test
    fun add_notes() {
        assertEquals(0, noteBoard.size)
        for (i in 0 .. 49) {
            noteBoard.add(Note(i, null, "$i",
                "body $i", false, null, 1, null, null))
        }
        assertEquals(50, noteBoard.size)
        for (i in 0 .. 49) {
            noteBoard.removeAt(0)
        }
        assertEquals(0, noteBoard.size)
    }

    @Test
    fun add_folders() {
        assertEquals(0, folders.size)
        for (i in 0 .. 49) {
            folders.add( Folder(i, listOf(), "${i+1}", 1, null))
        }
        assertEquals(50, noteBoard.size)
        for (i in 0 .. 49) {
            noteBoard.removeAt(0)
        }
        assertEquals(0, noteBoard.size)
    }
}