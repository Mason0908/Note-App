package com.example.noteapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecentlyDeletedActivity : AppCompatActivity() {
    private lateinit var noteBoard: RecyclerView
    private lateinit var notes: MutableList<Note>
    private lateinit var adapter: DeletedNoteAdapter
    private val db = DB(this, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recently_deleted)

        val actionBar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(actionBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Recently Deleted"

        val i = intent

        notes = db.getDeletedNotes()

        // Get reference for note list
        noteBoard = findViewById(R.id.noteBoard)

        // Tying with the adapter
        noteBoard.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        adapter = DeletedNoteAdapter(this, notes)
        noteBoard.adapter = adapter

        // Display list if exists
        if (notes.size > 0){
            displayList()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.recent_delete_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return true
            }
            R.id.deleteAllNotes -> {
                showDeleteForeverConfirmationDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteForeverConfirmationDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Empty recycle bin? This action cannot be undone.")
        builder.setPositiveButton("Yes", null)
        builder.setNegativeButton("Cancel", null)
        val dialog = builder.create()

        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                notes.forEach {
                    db.removeNote(it.id)
                }
                notes = db.getDeletedNotes()
                dialog.dismiss()
                displayList()
            }
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
                dialog.cancel()
            }
        }
        dialog.show()
    }

    private fun displayList() {
        noteBoard.layoutManager = LinearLayoutManager(this)
        val adapter = DeletedNoteAdapter(this, notes)
        noteBoard.adapter = adapter
    }
}