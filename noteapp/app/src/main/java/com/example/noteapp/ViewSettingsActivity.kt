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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.radiobutton.MaterialRadioButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat

/**
 * @Description Settings for a single note screen
 */

class ViewSettingsActivity : AppCompatActivity(), RadioGroup.OnCheckedChangeListener,
    AdapterView.OnItemClickListener {
    private var noteId: Int = -1
    private val db = DB(this, null)
    private val eventService = Retrofit.Builder()
        .baseUrl("https://noteapp-344119.uc.r.appspot.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .eventService

    private var listView: ListView? = null
    private var arrayAdapter: ArrayAdapter<String>? = null

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

        /*
        listView = findViewById(R.id.listColorHeading)
        arrayAdapter = ArrayAdapter(applicationContext,
            android.R.layout.simple_list_item_single_choice,
            resources.getStringArray(R.array.colorHeading))
        listView?.adapter = arrayAdapter
        listView?.choiceMode = ListView.CHOICE_MODE_SINGLE
        */

        setSupportActionBar(actionBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Settings"

        // Retrieve the note if exist
        val i = intent
        noteId = i.getIntExtra("editNoteId", -1)
        val colorHeading: String? = db.getHeadingColorOfNote(noteId)
        val colorBody: String? = db.getBodyColorOfNote(noteId)
        val font: String? = db.getFontOfNote(noteId)
        checkExisting(colorHeading, colorBody, font)
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var items: String = p0?.getItemAtPosition(p2) as String
        Toast.makeText(applicationContext, "Color Name : $items", Toast.LENGTH_LONG).show()
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
        when(p1) {
            R.id.black_body -> {
                db.editNoteSettings(noteId, colorBody, "#000000")
                GlobalScope.launch {
                    eventService.editNoteSetting(noteId.toLong(), colorBody, "#000000")
                }
            }
            R.id.red_body -> {
                db.editNoteSettings(noteId, colorBody, "#FF0000")
                GlobalScope.launch {
                    eventService.editNoteSetting(noteId.toLong(), colorBody, "#FF0000")
                }
            }
            R.id.blue_body -> {
                db.editNoteSettings(noteId, colorBody, "#0000FF")
                GlobalScope.launch {
                    eventService.editNoteSetting(noteId.toLong(), colorBody, "#0000FF")
                }
            }
            R.id.black_heading -> {
                db.editNoteSettings(noteId, colorHeading, "#000000")
                GlobalScope.launch {
                    eventService.editNoteSetting(noteId.toLong(), colorHeading, "#000000")
                }
            }
            R.id.red_heading -> {
                db.editNoteSettings(noteId, colorHeading, "#FF0000")
                GlobalScope.launch {
                    eventService.editNoteSetting(noteId.toLong(), colorHeading, "#FF0000")
                }
            }
            R.id.blue_heading -> {
                db.editNoteSettings(noteId, colorHeading, "#0000FF")
                GlobalScope.launch {
                    eventService.editNoteSetting(noteId.toLong(), colorHeading, "#0000FF")
                }
            }
            R.id.arial -> {
                db.editNoteSettings(noteId, font, "Arial")
                GlobalScope.launch {
                    eventService.editNoteSetting(noteId.toLong(), font, "Arial")
                }
            }
            R.id.times -> {
                db.editNoteSettings(noteId, font, "Times")
                GlobalScope.launch {
                    eventService.editNoteSetting(noteId.toLong(), font, "Times")
                }
            }
            R.id.courier -> {
                db.editNoteSettings(noteId, font, "Courier")
                GlobalScope.launch {
                    eventService.editNoteSetting(noteId.toLong(), font, "Courier")
                }
            }
        }
    }

    private fun checkExisting(colorHeading: String?, colorBody: String?, font: String?) {
        val radioButtonColorHeadingBlack: RadioButton = findViewById(R.id.black_heading)
        val radioButtonColorHeadingRed: RadioButton = findViewById(R.id.red_heading)
        val radioButtonColorHeadingBlue: RadioButton = findViewById(R.id.blue_heading)
        val radioButtonColorBodyBlack: RadioButton = findViewById(R.id.black_body)
        val radioButtonColorBodyRed: RadioButton = findViewById(R.id.red_body)
        val radioButtonColorBodyBlue: RadioButton = findViewById(R.id.blue_body)
        val radioButtonColorFontArial: RadioButton = findViewById(R.id.arial)
        val radioButtonColorFontTimes: RadioButton = findViewById(R.id.times)
        val radioButtonColorFontCourier: RadioButton = findViewById(R.id.courier)

        //println("$colorHeading, $colorBody, $font")
        when(colorHeading) {
            "#000000" -> {
                radioButtonColorHeadingBlack.isChecked = true
            }
            "#FF0000" -> {
                radioButtonColorHeadingRed.isChecked = true
            }
            "#0000FF" -> {
                radioButtonColorHeadingBlue.isChecked = true
            }
        }
        when(colorBody) {
            "#000000" -> {
                radioButtonColorBodyBlack.isChecked = true
            }
            "#FF0000" -> {
                radioButtonColorBodyRed.isChecked = true
            }
            "#0000FF" -> {
                radioButtonColorBodyBlue.isChecked = true
            }
        }
        when(font) {
            "Arial" -> {
                radioButtonColorFontArial.isChecked = true
            }
            "Times" -> {
                radioButtonColorFontTimes.isChecked = true
            }
            "Courier" -> {
                radioButtonColorFontCourier.isChecked = true
            }
        }
    }
}