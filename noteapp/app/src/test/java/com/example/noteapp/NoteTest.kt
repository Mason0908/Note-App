package com.example.noteapp

import kotlin.test.Test
import kotlin.test.assertEquals

internal class NoteTest {
    private val noteBoard: Model = Model()

    @Test
    fun initial_zero() {
        val expected = 0
        assertEquals(expected, noteBoard.getAllNotes().size)
    }

    @Test
    fun addNotes() {
        // create 100 notes
        for (i in 0 .. 99) {
            noteBoard.addNote("${i+1}", "")
        }
        val expected = 100
        assertEquals(expected, noteBoard.getAllNotes().size)
    }

    @Test
    fun deleteNote() {
        // create 100 notes
        for (i in 0 .. 99) {
            noteBoard.addNote("${i+1}", "")
        }
        for (i in 0 .. 29) {
            noteBoard.removeNote(i)
        }
        val expected = 70
        assertEquals(expected, noteBoard.getAllNotes().size)
    }

    @Test
    fun lockNote() {
        for (i in 0 .. 99) {
            noteBoard.addNote("${i+1}", "")
        }
        for (i in 50 .. 89) {
            noteBoard.lockNote(i, "123")
        }
        val expected = 40
        var countLock = 0
        for (i in 0 until noteBoard.getAllNotes().size) {
            if (noteBoard.getNoteById(i)?.isLocked == true) {
                countLock++
            }
        }
        assertEquals(expected, countLock)
    }

    @Test
    fun unlockNote() {
        for (i in 0 .. 99) {
            noteBoard.addNote("${i+1}", "")
        }
        for (i in 50 .. 89) {
            noteBoard.lockNote(i, "123")
        }
        for (i in 50 .. 59) {
            noteBoard.unlockNote(i)
        }
        val expected = 70
        var countUnlock = 0
        for (i in 0 until noteBoard.getAllNotes().size) {
            if (noteBoard.getNoteById(i)?.isLocked == false) {
                countUnlock++
            }
        }
        assertEquals(expected, countUnlock)
    }
}