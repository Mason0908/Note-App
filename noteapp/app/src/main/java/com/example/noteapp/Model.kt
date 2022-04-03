package com.example.noteapp

import android.app.Application
import com.example.common.Note
import com.example.common.Folder
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * @Description Underlying model class for monitoring notes
 */

class Model: Application() {
    private val notes: MutableList<Note> = mutableListOf()
    private val filteredNotes: MutableList<Note> = mutableListOf()
    private var _idCounter: Long = 0


    private fun generateID(): Long {
        return _idCounter++
    }

    fun getAllNotes(): MutableList<Note> {
        return notes
    }

    fun getNoteById(id: Long): Note? {
        return notes.find { it.id == id }
    }

    fun addNote(title: String, body: String, color: Int) {
        val noteToAdd: Note = Note(generateID(), null, title, body, false, null, color, "")
        notes.add(noteToAdd)
    }

    fun removeNote(id: Long) {
        notes.removeIf { it.id == id }
    }

    fun editNote(id: Long, newTitle: String, newBody: String) {
        var note = getNoteById(id)!!
        note.title = newTitle
        note.body = newBody
    }

    fun hasNote(id: Long): Boolean {
        notes.forEach {
            if (it.id == id) {
                return true
            }
        }
        return false
    }

    fun lockNote(id: Long, password: String) {
        val note = getNoteById(id)!!
        note.password = password
        note.isLocked = true
    }

    fun unlockNote(id: Long) {
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