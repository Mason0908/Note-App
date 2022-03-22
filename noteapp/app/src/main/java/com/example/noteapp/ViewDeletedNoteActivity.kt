package com.example.noteapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ViewDeletedNoteActivity : AppCompatActivity() {
    private lateinit var noteDisplay: TextView
    private var noteId: Int = -1
    private val db = DB(this, null)
    private val eventService = Retrofit.Builder()
        .baseUrl("https://noteapp-344119.uc.r.appspot.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .eventService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_deleted_note)

        noteDisplay = findViewById(R.id.noteDisplay)

        // the action bar with current note title and delete
        val actionBar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(actionBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Retrieve the note if exist
        val i = intent
        noteId = i.getIntExtra("deletedNoteId", -1)

        if (noteId >= 0) {
            println("DELETED NOTE ID: $noteId")
            val currNote = db.getNoteById(noteId)!!
            supportActionBar!!.title = currNote.title
            noteDisplay.text = currNote.body
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                startActivity(Intent(this, RecentlyDeletedActivity::class.java))
                finish()
                return true
            }
            R.id.restoreNote -> {
                val i = Intent(this, MoveNoteActivity::class.java)
                i.putExtra("noteToBeMoved", noteId)
                i.putExtra("isRestore", true)
                startActivity(i)
                finish()
                return true
            }
            R.id.deleteForeverNote -> {
                showDeleteForeverConfirmationDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteForeverConfirmationDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("This note will be deleted forever. This action cannot be undone.")
        builder.setPositiveButton("Delete", null)
        builder.setNegativeButton("Cancel", null)
        val dialog = builder.create()

        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                db.removeNote(noteId)
                GlobalScope.launch {
                    eventService.removeNote(noteId.toLong())
                }
                dialog.dismiss()
                startActivity(Intent(this, RecentlyDeletedActivity::class.java))
                finish()
            }
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
                dialog.cancel()
            }
        }
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.view_deleted_note_menu, menu)
        return true
    }
}