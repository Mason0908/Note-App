package com.example.noteapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ViewNoteActivity : AppCompatActivity() {
    private lateinit var noteDisplay: TextView
    private var noteId: Int = -1
    private val db = DB(this, null)
    private var folderId: Int = -1
    private lateinit var tagBoard: RecyclerView
    private var tags: String = ""
    private lateinit var adapter: TagAdapterForView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_note)

        noteDisplay = findViewById(R.id.noteDisplay)

        // the action bar with current note title and delete
        val actionBar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(actionBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Retrieve the note if exist
        val i = intent
        noteId = i.getIntExtra("displayId", -1)
        folderId = i.getIntExtra("folderId", -1)
        // println("In view note, folderId is $folderId")
        if (noteId >= 0){
            val currNote = db.getNoteById(noteId)!!
            if (currNote.isLocked) {
                showLockedNoteAlert(currNote)
            } else {
                supportActionBar!!.title = currNote.title
                noteDisplay.text = currNote.body
            }
            tags = db.getTags(noteId)
        }
        // Get reference for tag list
        tagBoard = findViewById(R.id.tagBoard)

        // Tying with the adapter
        tagBoard.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        adapter = TagAdapterForView(this, tags)
        tagBoard.adapter = adapter

        if (tags.isNotEmpty()){
            displayTagsList()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                val i = Intent(this, MainActivity::class.java)
                i.putExtra("folderId", folderId)
                startActivity(i)
                finish()
                return true
            }
            R.id.lockNote -> {
                val note = db.getNoteById(noteId)!!
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
                    db.removeNote(noteId)
                }
                val i = Intent(this, MainActivity::class.java)
                i.putExtra("folderId", folderId)
                startActivity(i)
                finish()
                return true
            }
            R.id.editNote -> {
                val i = Intent(this, AddNoteActivity::class.java)
                i.putExtra("editId", noteId)
                i.putExtra("folderId", folderId)
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
                db.unlockNote(note.id)
                Toast.makeText(this, "Password removed!", Toast.LENGTH_SHORT).show()
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
                } else {
                    if (isReset) {
                        Toast.makeText(this, "Password reset!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Password created!", Toast.LENGTH_SHORT).show()
                    }
                    db.lockNote(note.id, input.text.toString())
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
        val btnShowHide: Button = findViewById(R.id.btnShowHide)
        val btnCancel: Button = findViewById(R.id.btnCancel)
        val btnOK: Button = findViewById(R.id.btnOK)
        val pwd: EditText = findViewById(R.id.pwd)
        btnShowHide.setOnClickListener {
            if(btnShowHide.text.toString() == "Show"){
                pwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
                btnShowHide.text = "Hide"
            } else{
                pwd.transformationMethod = PasswordTransformationMethod.getInstance()
                btnShowHide.text = "Show"
            }
        }
        btnCancel.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        btnOK.setOnClickListener {
            if (pwd.text.toString() != note.password) {
                pwd.error = "Password incorrect!"
            } else {
                //get view note again
                setContentView(R.layout.activity_view_note)
                noteDisplay = findViewById(R.id.noteDisplay)
                // the action bar with current note title and delete
                val actionBar: Toolbar = findViewById(R.id.toolbar)
                setSupportActionBar(actionBar)
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.title = note.title
                noteDisplay.text = note.body
                Toast.makeText(this, "Note unlocked!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayTagsList() {
        tags = db.getTags(noteId)
        tagBoard.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        adapter = TagAdapterForView(this, tags)
        tagBoard.adapter = adapter
    }
}