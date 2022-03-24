package com.example.noteapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.Color
import android.icu.text.CaseMap
import android.text.InputType
import android.view.Menu
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
 * @Description Add/Edit folder screen
 */

class AddEditFolderActivity : AppCompatActivity() {
    private lateinit var titleField: EditText
    private var folderId: Int = -1
    private val db = DB(this, null)
    private val eventService = Retrofit.Builder()
        .baseUrl("https://noteapp-344119.uc.r.appspot.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .eventService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_folder)
        titleField = findViewById(R.id.folderName)

        // showing the add note icon, add tag icon(TO-DO) and back button in action bar
        val actionBar:Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(actionBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "New Folder"

        // Retrieve the folder if exist
        val i = intent
        folderId = i.getIntExtra("editFolderId", -1)
        if (folderId >= 0) {
            val currFolder = db.getFolderById(folderId)
            titleField.setText(currFolder?.title)
            supportActionBar!!.title = currFolder?.title
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                finish()
                return true
            }

            R.id.saveChanges -> {
                if (titleField.text.toString().isNullOrEmpty()) {
                    titleField.error = "Folder title cannot be empty"
                    return true
                }
                if (!db.hasFolder(folderId)) {
                    val color = generateColour()
                    db.addFolder(titleField.text.toString(), color)
                    val id = db.getLatestFolder()?.id
                    GlobalScope.launch {
                        eventService.addFolder(id ?: 0, titleField.text.toString(), color)
                    }
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    db.editFolder(folderId, titleField.text.toString())
                    val color = db.getFolderById(folderId)?.color
                    GlobalScope.launch {
                        eventService.addFolder(folderId.toLong(), titleField.text.toString(), color ?: generateColour())
                    }
                    val i = Intent(this, ViewFolderActivity::class.java)
                    i.putExtra("goBackFolder", folderId)
                    startActivity(i)
                }
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

}
