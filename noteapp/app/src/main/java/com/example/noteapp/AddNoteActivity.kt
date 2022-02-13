package com.example.noteapp

import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * @Description Add/Edit note screen
 */

class AddNoteActivity : AppCompatActivity() {
    private lateinit var titleField: EditText
    private lateinit var bodyField: EditText
    private var noteId: Int = -1

    private lateinit var btnSave: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addnote)
        titleField = findViewById(R.id.noteTitle)
        bodyField = findViewById(R.id.noteBody)

        // showing the add note icon, add tag icon(TO-DO) and back button in action bar
        val actionBar:Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(actionBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "New Note"

        // Retrieve the note if exist
        val i = intent
        noteId = i.getIntExtra("editId", -1)
        if (noteId >= 0) {
            val currNote = (this.application as Model).getNoteById(noteId)
            titleField.setText(currNote?.title)
            bodyField.setText(currNote?.body)
            supportActionBar!!.title = currNote?.title
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return true
            }
            //Todo: enable adding tags
            R.id.addTag -> {
                return true
            }
            R.id.saveChanges -> {
                val app = this.application as Model
                if (!app.hasNote(noteId)) {
                    app.addNote(titleField.text.toString(), bodyField.text.toString())
                } else {
                    app.editNote(noteId, titleField.text.toString(), bodyField.text.toString())
                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.save_menu, menu)
        return true
    }
}