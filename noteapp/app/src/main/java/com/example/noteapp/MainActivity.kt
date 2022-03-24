package com.example.noteapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.radiobutton.MaterialRadioButton
import java.text.SimpleDateFormat

/**
 * @Description Home screen
 */
class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var noteBoard: RecyclerView
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var btnAddNote: FloatingActionButton
    private lateinit var btnAddFolder: FloatingActionButton
    private lateinit var notes: MutableList<Note>
    private lateinit var folders: MutableList<Folder>
    private lateinit var adapter: Adapter
    private var sortBy = ""
    private var sortMethod = ""
    private val db = DB(this, null)
    private var isFABOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notes = db.getNotesWithNoFolder()
        folders = db.getAllFolders()
        //println("folder: " + folders)

        // Get reference for note list
        noteBoard = findViewById(R.id.noteBoard)

        // Tying with the adapter
        noteBoard.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        adapter = Adapter(this, notes, folders)
        noteBoard.adapter = adapter

        val actionBar: Toolbar = findViewById(R.id.toolbar)
        // showing the back button in action bar
        setSupportActionBar(actionBar)
        supportActionBar!!.title = "Main Board"

        // Get reference for add button
        btnAddFolder = findViewById(R.id.btnAddFolder)
        btnAddFolder.setOnClickListener {
            startActivity(Intent(this, AddEditFolderActivity::class.java))
            finish()
        }
        btnAddNote = findViewById(R.id.btnAddNote)
        btnAddNote.setOnClickListener {
            val i = Intent(this, AddEditNoteActivity::class.java)
            i.putExtra("backMain", true)
            startActivity(i)
            finish()
        }
        btnAdd = findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener {
            if (!isFABOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }

        // Display list if exists
        if (notes.size > 0 || folders.size > 0){
            displayList()
        }
    }

    private fun showFABMenu() {
        isFABOpen = true
        btnAddNote.animate().translationY(-resources.getDimension(R.dimen.standard_65))
        btnAddFolder.animate().translationY(-resources.getDimension(R.dimen.standard_125))
    }

    private fun closeFABMenu() {
        isFABOpen = false
        btnAddNote.animate().translationY(0F)
        btnAddFolder.animate().translationY(0F)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_board_menu, menu)
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
            if (sortBy != ""){
                notes = db.getSearchNotes(newText, sortBy, sortMethod)
                folders = db.getSearchFolders(newText, sortBy, sortMethod)
            }
            else {
                notes = db.getSearchNotes(newText)
                folders = db.getSearchFolders(newText)
            }
            displayList()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.searchIcon -> {

            }
            R.id.recycleBinIcon -> {
                startActivity(Intent(this, RecentlyDeletedActivity::class.java))
                finish()
                return true
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
                        folders.sortBy { it.title }
                        notes.sortBy { it.title }
                        sortBy = "title"
                        sortMethod = "ASC"
                    }
                R.id.sort_za ->
                    if (checked) {
                        folders.sortByDescending { it.title }
                        notes.sortByDescending { it.title }
                        sortBy = "title"
                        sortMethod = "DESC"
                    }
                R.id.sort_date ->
                    if (checked){
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        folders.sortByDescending { dateFormat.parse(it.modify_date) }
                        notes.sortByDescending { dateFormat.parse(it.modify_date) }
                        sortBy = "modify_date"
                        sortMethod = "DESC"
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
        val adapter = Adapter(this, notes, folders)
        noteBoard.adapter = adapter
    }


}