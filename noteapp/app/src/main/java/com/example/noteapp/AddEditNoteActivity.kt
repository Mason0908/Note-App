package com.example.noteapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
//import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


/**
 * @Description Add/Edit note screen
 */

class AddEditNoteActivity : AppCompatActivity() {
    private lateinit var titleField: EditText
    private lateinit var bodyField: EditText
    private var noteId: Int = -1
    private var folderId: Int? = -1
    private var backMain: Boolean = true
    private val db = DB(this, null)
    var tags: String = ""
    private lateinit var tagBoard: RecyclerView
    private lateinit var adapter: TagAdapterForEdit

    private val eventService = Retrofit.Builder()
        .baseUrl("https://noteapp-344119.uc.r.appspot.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .eventService

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
        noteId = i.getIntExtra("editNoteId", -1)
        folderId = i.getIntExtra("currFolderId", -1)
        backMain = i.getBooleanExtra("backMain", true)
        if (noteId >= 0) {
            val currNote = db.getNoteById(noteId)
            titleField.setText(currNote?.title)
            bodyField.setText(currNote?.body)
            supportActionBar!!.title = currNote?.title
            tags = db.getTags(noteId)
        }
        if (folderId!! < 0) {
            folderId = db.getFolderIdOfNote(noteId)
        }
        // Get reference for tag list
        tagBoard = findViewById(R.id.tagBoard)

        // Tying with the adapter
        tagBoard.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        adapter = TagAdapterForEdit(this, tags, tagBoard, this)
        tagBoard.adapter = adapter

        if (tags.isNotEmpty()){
            displayTagsListInProgress(tags)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                if (noteId == -1) {
                    if(backMain) {
                        val i = Intent(this, MainActivity::class.java)
                        startActivity(i)
                        finish()
                        return true
                    } else {
                        val i = Intent(this, ViewFolderActivity::class.java)
                        i.putExtra("displayFolderId", folderId)
                        startActivity(i)
                        finish()
                        return true
                    }
                } else {
                    val i = Intent(this, ViewNoteActivity::class.java)
                    i.putExtra("displayNoteId", noteId)
                    startActivity(i)
                    finish()
                    return true
                }

            }
            R.id.addTag -> {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle("Create tag")
                val input = EditText(this)
                input.inputType = InputType.TYPE_CLASS_TEXT
                builder.setView(input)
                builder.setPositiveButton("OK", null)
                builder.setNegativeButton("Cancel", null)
                val dialog = builder.create()

                dialog.setOnShowListener {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                        if (!db.hasNote(noteId)) {
                            if (input.text.toString().isNullOrEmpty()) {
                                input.error = "Tag name cannot be empty"
                            } else {
                                val tag = input.text.toString()
                                val listOfTags = tags.split(",")
                                if (listOfTags.contains(tag)) {
                                    input.error = "Tag already exists"
                                } else {
                                    tags += "$tag,"
                                    displayTagsListInProgress(tags)
                                    dialog.dismiss()
                                }
                            }
                        } else {
                            if (input.text.toString().isNullOrEmpty()) {
                                input.error = "Tag name cannot be empty"
                            } else {
                                val tag = input.text.toString()
                                val listOfTags = tags.split(",")
                                if (db.hasTag(tag, noteId) || listOfTags.contains(tag)) {
                                    input.error = "Tag already exists"
                                } else {
                                    tags += "$tag,"
                                    displayTagsListInProgress(tags)
                                    dialog.dismiss()
                                }
                            }
                        }
                    }
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
                        dialog.cancel()
                    }
                }
                dialog.show()
                return true
            }
            R.id.saveChanges -> {
                if (!db.hasNote(noteId)) {
                    val color = generateColour()
                    db.addNote(titleField.text.toString(), bodyField.text.toString(), color, tags, folderId)
                    val id = db.getLatestNote()?.id
                    GlobalScope.launch {
                        eventService.addNote(id ?: 0, titleField.text.toString(), bodyField.text.toString(), color, tags, folderId)
                    }
                } else {
                    val color = db.getNoteById(noteId)?.color
                    val currFolderId = when(db.getNoteById(noteId)?.folderId){
                        0 -> null
                        else -> db.getNoteById(noteId)?.folderId
                    }
                    db.editNote(noteId, titleField.text.toString(), bodyField.text.toString(), tags)
                    GlobalScope.launch {
                        eventService.addNote(noteId.toLong(), titleField.text.toString(), bodyField.text.toString(), color ?: generateColour(), tags, currFolderId)
                    }
                }
                val i = Intent(this, ViewNoteActivity::class.java)
                i.putExtra("displayNoteId", when(noteId){
                    -1 -> db.getLatestNote()?.id?.toInt()
                    else -> noteId
                })
                startActivity(i)
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

    private fun generateColour(): Int {
        val num = (1..4).random()
        var color = 0
        when(num) {
            1 -> {
                color = -396315
            }
            2 -> {
                color = -3858
            }
            3 -> {
                color = -264765
            }
            4 -> {
                color = -3083024
            }
        }
        return color
    }

//    private fun displayTagsList() {
//        tagBoard.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
//        adapter = TagAdapter(this, tags, tagBoard)
//        tagBoard.adapter = adapter
//    }

    fun displayTagsListInProgress(tags: String) {
        tagBoard.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        adapter = TagAdapterForEdit(this, tags, tagBoard, this)
        tagBoard.adapter = adapter
    }
}
