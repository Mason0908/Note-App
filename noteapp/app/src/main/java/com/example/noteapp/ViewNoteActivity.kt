package com.example.noteapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class ViewNoteActivity : AppCompatActivity() {
    private lateinit var noteDisplay: TextView
    private var noteId: Int = -1
    private lateinit var app: Model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_note)

        app = this.application as Model
        noteDisplay = findViewById(R.id.noteDisplay)

        // the action bar with current note title and delete
        val actionBar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(actionBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Retrieve the note if exist
        val i = intent
        noteId = i.getIntExtra("displayId", -1)
        if (noteId >= 0){
            val currNote = app.getNoteById(noteId)!!
            if (currNote.isLocked) {
                showLockedNoteAlert(currNote)
            } else {
                supportActionBar!!.title = currNote.title
                noteDisplay.text = currNote.body
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return true
            }
            R.id.lockNote -> {
                val note = app.getNoteById(noteId)!!
                if (note.isLocked) {
                    showResetOrDeleteAlert(note)
                } else {
                    showPasswordWindow(note, false)
                }
                return true
            }
            R.id.deleteNote -> {
                // Remove only if the id exists
                if (noteId >= 0){
                    app.removeNote(noteId)
                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return true
            }
            R.id.editNote -> {
                val i = Intent(this, AddNoteActivity::class.java)
                i.putExtra("editId", noteId)
                startActivity(i)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.view_menu, menu)
        return true
    }

    fun showResetOrDeleteAlert(note: Note) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Password is already created.")
        builder.setPositiveButton("Reset Password", null)
        builder.setNegativeButton("Remove Password", null)
        builder.setNeutralButton("Cancel", null)
        val dialog = builder.create()

        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                dialog.dismiss()
                showPasswordWindow(note, true)
            }
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
                app.unlockNote(note.id)
                dialog.cancel()
            }
            dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {
                dialog.cancel()
            }
        }

        dialog.show()
    }

    fun showPasswordWindow(note: Note, isReset: Boolean) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        if (isReset) {
            builder.setTitle("Reset Password")
        } else {
            builder.setTitle("Create Password")
        }
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("OK", null)
        builder.setNegativeButton("Cancel", null)
        val dialog = builder.create()

        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                if (input.text.toString().isNullOrEmpty()) {
                    input.error = "Password cannot be empty"
                    //Toast.makeText(this, "empty", Toast.LENGTH_SHORT).show()
                } else {
                    //Toast.makeText(this, "yeah!", Toast.LENGTH_SHORT).show()
                    app.lockNote(note.id, input.text.toString())
                    dialog.dismiss()
                }
            }
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
                dialog.cancel()
            }
        }

        dialog.show()
    }

    fun showLockedNoteAlert(note: Note) {
        this.setContentView(R.layout.activity_lock_note)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("This note is locked. Please enter the password.")
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("OK", null)
        builder.setNegativeButton("Cancel", null)
        val dialog = builder.create()

        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                if (input.text.toString() != note.password) {
                    input.error = "Password incorrect!"
                } else {
                    // initialize note view again
                    setContentView(R.layout.activity_view_note)
                    noteDisplay = findViewById(R.id.noteDisplay)
                    // the action bar with current note title and delete
                    val actionBar: Toolbar = findViewById(R.id.toolbar)
                    setSupportActionBar(actionBar)
                    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                    supportActionBar!!.title = note.title
                    noteDisplay.text = note.body
                    dialog.dismiss()
                }
            }
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
                dialog.cancel()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        dialog.show()
    }
}