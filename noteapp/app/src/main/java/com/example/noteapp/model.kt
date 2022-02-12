package com.example.noteapp

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * @Description Underlying model class for monitoring notes
 */

class model: Application() {
    private val notes: MutableList<Note> = mutableListOf()
    private var _idCounter: Int = 0

    private fun generateID(): Int {
        return _idCounter++
    }

    fun getAllNotes(): MutableList<Note> {
        return notes
    }
    fun getNoteById(id: Int): Note? {
        return notes.find { it.id == id }
    }

    fun addNote(title: String, body: String) {
        val noteToAdd: Note = Note(generateID(), null, title, body)
        notes.add(noteToAdd)
    }

    fun removeNote(id: Int) {
        notes.removeIf { it.id == id }
    }
}