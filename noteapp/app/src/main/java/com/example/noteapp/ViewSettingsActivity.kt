package com.example.noteapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.radiobutton.MaterialRadioButton
import java.text.SimpleDateFormat

/**
 * @Description Settings for a single note screen
 */

class ViewSettingsActivity : AppCompatActivity(), RadioGroup.OnCheckedChangeListener {
    private var noteId: Int = -1
    private val db = DB(this, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_note)

        val actionBar:Toolbar = findViewById(R.id.toolbar)
        val radioGroupColorHeading: RadioGroup = findViewById(R.id.radioGroup)
        radioGroupColorHeading.setOnCheckedChangeListener(this)

        val radioGroupColorBody: RadioGroup = findViewById(R.id.radioGroup2)
        radioGroupColorBody.setOnCheckedChangeListener(this)

        val radioGroupFont: RadioGroup = findViewById(R.id.radioGroup3)
        radioGroupFont.setOnCheckedChangeListener(this)

        setSupportActionBar(actionBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Settings"

        // Retrieve the note if exist
        val i = intent
        noteId = i.getIntExtra("editNoteId", -1)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val i = Intent(this, ViewNoteActivity::class.java)
                i.putExtra("displayNoteId", noteId)
                startActivity(i)
                finish()
                return true
            }
            R.id.saveChanges -> {
                val i = Intent(this, ViewNoteActivity::class.java)
                i.putExtra("displayNoteId", noteId)
                startActivity(i)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
        // p1 is the id the radiobutton that got clicked
        val colorHeading: String = "color_heading"
        val colorBody: String = "color_body"
        val font: String = "font"
        println(p1)
        when(p1) {
            R.id.black_body ->
                db.editNoteSettings(noteId, colorBody, "#000000")
            R.id.red_body ->
                db.editNoteSettings(noteId, colorBody, "#FF0000")
            R.id.blue_body ->
                db.editNoteSettings(noteId, colorBody, "#0000FF")
            R.id.black_heading ->
                db.editNoteSettings(noteId, colorHeading, "#000000")
            R.id.red_heading ->
                db.editNoteSettings(noteId, colorHeading, "#FF0000")
            R.id.blue_heading ->
                db.editNoteSettings(noteId, colorHeading, "#0000FF")
            R.id.arial ->
                db.editNoteSettings(noteId, font, "Arial")
            R.id.times ->
                db.editNoteSettings(noteId, font, "Times")
            R.id.courier ->
                db.editNoteSettings(noteId, font, "Courier")
        }
    }

}