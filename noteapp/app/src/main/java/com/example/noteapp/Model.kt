package com.example.noteapp

import android.app.Application
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi

/**
 * @Description Underlying model class for monitoring notes
 */

class Model: Application() {
    private val notes: MutableList<Note> = mutableListOf()
    private val filteredNotes: MutableList<Note> = mutableListOf()
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
        val noteToAdd: Note = Note(generateID(), null, title, body, false, null)
        notes.add(noteToAdd)
    }

    fun removeNote(id: Int) {
        notes.removeIf { it.id == id }
    }

    fun editNote(id: Int, newTitle: String, newBody: String) {
        var note = getNoteById(id)!!
        note.title = newTitle
        note.body = newBody
    }

    fun hasNote(id: Int): Boolean {
        notes.forEach {
            if (it.id == id) {
                return true
            }
        }
        return false
    }

    fun lockNote(id: Int, password: String) {
        val note = getNoteById(id)!!
        note.password = password
        note.isLocked = true
    }

    fun unlockNote(id: Int) {
        val note = getNoteById(id)!!
        note.password = null
        note.isLocked = false
    }

    private fun search(criteria: String) {
        filteredNotes.clear()
        filteredNotes.addAll(notes.filter { note ->
            note.body.contains(
                criteria,
                true
            ) || note.title.contains(criteria, true)
        } as MutableList<Note>)
    }

    fun getSearchNotes(criteria: String): MutableList<Note> {
        search(criteria)
        return filteredNotes
    }
}