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

/**
 * @Description Add/Edit folder screen
 */

class AddFolderActivity : AppCompatActivity() {
    private lateinit var titleField: EditText
    private lateinit var bodyField: EditText
    private var folderId: Int = -1
    private val db = DB(this, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addfolder)
        titleField = findViewById(R.id.folderName)

        // showing the add note icon, add tag icon(TO-DO) and back button in action bar
        val actionBar:Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(actionBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "New Folder"

        // Retrieve the folder if exist
        val i = intent
        folderId = i.getIntExtra("editId", -1)
        if (folderId >= 0) {
            val currFolder = db.getFolderById(folderId)
            titleField.setText(currFolder?.title)
            supportActionBar!!.title = currFolder?.title
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                startActivity(Intent(this, FolderActivity::class.java))
                finish()
                return true
            }
            R.id.saveChanges -> {
                if (!db.hasFolder(folderId)) {
                    db.addFolder(titleField.text.toString(), generateColour())
                } else {
                    db.editFolder(folderId, titleField.text.toString())
                }
                startActivity(Intent(this, FolderActivity::class.java))
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
        var color: Int = 0
        when(num) {
            1 -> {
                color = R.color.lightyellow
            }
            2 -> {
                color = R.color.lightblue
            }
            3 -> {
                color = R.color.lightgreyyellow
            }
            4 -> {
                color = R.color.lightpink
            }
        }
        return color
    }

}