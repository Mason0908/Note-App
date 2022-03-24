package com.example.noteapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
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
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.tiagohm.markdownview.MarkdownView
import br.tiagohm.markdownview.css.InternalStyleSheet
import br.tiagohm.markdownview.css.styles.Github


class ViewNoteActivity : AppCompatActivity() {
    private lateinit var noteDisplay: MarkdownView
    private var noteId: Int = -1
    private var folderId: Int? = null
    private val db = DB(this, null)
    private lateinit var tagBoard: RecyclerView
    private lateinit var wordCount: TextView

    private var tags: String = ""
    private lateinit var adapter: TagAdapterForView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_note)

        noteDisplay = findViewById(R.id.noteDisplay)
        wordCount = findViewById(R.id.wordCount)

        // the action bar with current note title and delete
        val actionBar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(actionBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Retrieve the note if exist
        val i = intent
        noteId = i.getIntExtra("displayNoteId", -1)
        if (noteId >= 0){
            val currNote = db.getNoteById(noteId)!!
            if (currNote.isLocked) {
                showLockedNoteAlert(currNote)
            } else {
                supportActionBar!!.title = currNote.title
                //noteDisplay.addStyleSheet(Github())
                val css: InternalStyleSheet = cssStyleSheet(currNote.color_heading, currNote.color_body, currNote.font)
                noteDisplay.addStyleSheet(css)
                val body: String? = currNote?.body
                val words: String? = body?.trim()
                val lines: List<String>? = body?.lines()
                val frequencyMap: MutableMap<String, Int> = HashMap()
                for (s in lines!!) {
                    var count = frequencyMap[s]
                    if (count == null) count = 0
                    frequencyMap[s] = count + 1
                }
                var linesToRemove: Int = 0;
                if (frequencyMap.containsKey("")) {
                    linesToRemove = frequencyMap[""]!!
                }
                if (body.length == 0) {
                    wordCount.setText("     Number of words: 0     Number of lines: 0")
                } else {
                wordCount.setText(
                        "\n     Number of words: " + words?.split("\\s+".toRegex())?.size +
                        "     Number of lines: " + (body?.lines()?.size!! - linesToRemove))

                noteDisplay.loadMarkdown(currNote.body)
                    noteDisplay.addStyleSheet(css)


                //noteDisplay.text = currNote.body
                }
            }
            tags = db.getTags(noteId)
            if (db.noteHasFolder(noteId)) {
                folderId = db.getFolderIdOfNote(noteId)
            }
        }
        // Get reference for tag list
        if (!db.getNoteById(noteId)!!.isLocked) {
            tagBoard = findViewById(R.id.tagBoard)

            // Tying with the adapter
            tagBoard.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
            adapter = TagAdapterForView(this, tags)
            tagBoard.adapter = adapter

            if (tags.isNotEmpty()){
                displayTagsList()
            }
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.settings -> {
                val i = Intent(this, ViewSettingsActivity::class.java)
                i.putExtra("editNoteId", noteId)
                startActivity(i)
            }
            android.R.id.home -> {
                if (folderId != null) {
                    val i = Intent(this, ViewFolderActivity::class.java)
                    i.putExtra("goBackFolder", folderId)
                    i.putExtra("backMain", 0)
                    startActivity(i)
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                finish()
                return true
            }
            R.id.moveNote -> {
                val i = Intent(this, MoveNoteActivity::class.java)
                i.putExtra("noteToBeMoved", noteId)
                i.putExtra("isRestore", false)
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
                    db.removeNoteTemporarily(noteId)
                }
                if (folderId != null) {
                    val i = Intent(this, ViewFolderActivity::class.java)
                    i.putExtra("goBackFolder", folderId)
                    startActivity(i)
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                finish()
                return true
            }
            R.id.editNote -> {
                val markdownView: MarkdownView = findViewById(R.id.noteDisplay)
                markdownView.addStyleSheet(Github())
                markdownView.loadMarkdown("**MarkdownView**")
                val typeface: Typeface? = ResourcesCompat.getFont(this, R.font.jacksimba)
                //markdownView.setTypeface(typeface)
                println("editing note")
                val i = Intent(this, AddEditNoteActivity::class.java)
                i.putExtra("editNoteId", noteId)
                //i.putExtra("editFolderId", folderId)
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
            } else {
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
                noteDisplay.loadMarkdown(note.body)
                //noteDisplay.text = note.body
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

    private fun cssStyleSheet(colorHeading: String?, colorBody: String?, font: String?): InternalStyleSheet {
        val css: InternalStyleSheet = Github()
        //css.addFontFace("MyFont", "condensed", "italic", "bold", "url('myfont.ttf')")
        //css.addMedia("screen and (min-width: 1281px)")
        //css.addRule("h1", "color: blue")
        println("should change color")
        //css.endMedia()
        css.addRule("h1", "color: $colorHeading", "font-family: $font")
        css.addRule("h2", "color: $colorHeading", "font-family: $font")
        css.addRule("h3", "color: $colorHeading", "font-family: $font")
        css.addRule("h4", "color: $colorHeading", "font-family: $font")
        css.addRule("h5", "color: $colorHeading", "font-family: $font")
        css.addRule("h6", "color: $colorHeading", "font-family: $font")
        css.addRule("*", "color: $colorBody", "font-family: $font")
        println("rule should be added")
        return css
    }
}