package com.example.noteapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * @Description Home screen
 */
class MainActivity : AppCompatActivity() {
    private lateinit var noteBoard: RecyclerView
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var notes: MutableList<Note>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notes = (this.application as model).getAllNotes()

        val actionBar: Toolbar = findViewById(R.id.toolbar)
        // showing the back button in action bar
        setSupportActionBar(actionBar)
        supportActionBar!!.title = "记事簿"

        // Get reference for add button
        btnAdd = findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
            finish()
        }

        // Get reference for note list
        noteBoard = findViewById(R.id.noteBoard)

        // Display list if exists
        if (notes.size > 0){
            displayList()
        }
    }

    /**
     * @Description Display all notes in the database
     * @author Mason
     * @return void
     */
    private fun displayList() {
        noteBoard.setLayoutManager(LinearLayoutManager(this))
        val adapter = Adapter(this, notes)
        noteBoard.setAdapter(adapter)
    }
}