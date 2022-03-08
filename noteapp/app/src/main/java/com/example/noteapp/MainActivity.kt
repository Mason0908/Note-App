package com.example.noteapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ActionMenuView
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.RadioButton
import com.google.android.material.radiobutton.MaterialRadioButton
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Description Home screen
 */

/**
 * TODO: figure out where exactly we want to delete or edit a folder (in the note main page?)
 */

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var noteBoard: RecyclerView
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var notes: MutableList<Note>
    private var folderId: Int = -1
    private lateinit var adapter: Adapter
    private val db = DB(this, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val i = intent
        folderId = i.getIntExtra("folderId", -1)
        // println("folderID in main activity: $folderId")

        notes = db.getAllFolderNotesObject(folderId)!!
        // println("notes: $notes")
        val testNotes = db.getAllNotes()

        // Get reference for note list
        noteBoard = findViewById(R.id.noteBoard)

        // Tying with the adapter
        noteBoard.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        adapter = Adapter(this, notes)
        noteBoard.adapter = adapter

        val actionBar: Toolbar = findViewById(R.id.toolbar)
        // showing the back button in action bar
        setSupportActionBar(actionBar)
        supportActionBar!!.title = "My Notes"

        // Get reference for add button
        btnAdd = findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val i = Intent(this, AddNoteActivity::class.java)
            i.putExtra("folderId", folderId)
            startActivity(i)
            finish()
        }

        // Display list if exists
        if (notes.size > 0){
            displayList()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)
        val search = menu?.findItem(R.id.searchIcon)
        val searchView = search?.actionView as SearchView
        searchView.isSubmitButtonEnabled = false
        searchView.setOnQueryTextListener(this)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null){
            notes = db.getSearchNotes(newText, folderId)
            displayList()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.searchIcon -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onRadioButtonClicked(view: View) {
        if (view is MaterialRadioButton) {
            val checked = view.isChecked

            when (view.getId()) {
                R.id.sort_az ->
                    if (checked) {
                        notes.sortBy { it.title }
                    }
                R.id.sort_za ->
                    if (checked) {
                        notes.sortByDescending { it.title }
                    }
                R.id.sort_date ->
                    if (checked) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        notes.sortByDescending { dateFormat.parse(it.modify_date) }
                    }
            }
            displayList()
        }
    }

    /**
     * @Description Set up adapter for recycle view
     * @author Mason
     * @return void
     */
    private fun displayList() {
        noteBoard.layoutManager = LinearLayoutManager(this)
        val adapter = Adapter(this, notes)
        noteBoard.adapter = adapter
    }

}