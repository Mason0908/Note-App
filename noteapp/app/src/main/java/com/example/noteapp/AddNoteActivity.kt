package com.example.noteapp

import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.noteapp.R
import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
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

        // Retrieve the note if exist
        val i = intent
        noteId = i.getIntExtra("id", -1)
        if (noteId >= 0){
            val currNote = (this.application as model).getNoteById(noteId)
            titleField.setText(currNote?.title)
            bodyField.setText(currNote?.body)
        }

        // showing the back button in action bar
        val actionBar:Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(actionBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "New Note"




    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return true
            }
            R.id.deleteChanges -> {
                // Remove only if the id exists
                if (noteId >= 0){
                    (this.application as model).removeNote(noteId)
                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return true
            }
            R.id.saveChanges -> {
                (this.application as model).addNote(titleField.text.toString(), bodyField.text.toString())
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