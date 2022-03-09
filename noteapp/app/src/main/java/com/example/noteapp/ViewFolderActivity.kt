package com.example.noteapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.radiobutton.MaterialRadioButton
import java.text.SimpleDateFormat

class ViewFolderActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var noteBoard: RecyclerView
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var notes: MutableList<Note>
    private var folderId: Int = -1
    private lateinit var adapter: NoteAdapter
    private val db = DB(this, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_folder)

        val actionBar: Toolbar = findViewById(R.id.toolbar)
        // showing the back button in action bar
        setSupportActionBar(actionBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val i = intent
        folderId = i.getIntExtra("displayFolderId", -1)
        if (folderId >= 0){
            val currFolder = db.getFolderById(folderId)!!
            supportActionBar!!.title = currFolder.title
        } else {
            folderId = i.getIntExtra("goBackFolder", -1)
            if (folderId >= 0) {
                val currFolder = db.getFolderById(folderId)
                if (currFolder != null) {
                    supportActionBar!!.title = currFolder.title
                }
            }
        }


        notes = db.getAllFolderNotesObject(folderId)!!

        // Get reference for note list
        noteBoard = findViewById(R.id.noteBoard)

        // Tying with the adapter
        noteBoard.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        adapter = NoteAdapter(this, notes)
        noteBoard.adapter = adapter

        // Get reference for add button
        btnAdd = findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val i = Intent(this, AddEditNoteActivity::class.java)
            i.putExtra("currFolderId", folderId)
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
        inflater.inflate(R.menu.folder_menu, menu)
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
            notes = db.getSearchNotesInFolders(newText, folderId)
            displayList()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return true
            }
            R.id.searchIcon -> {

            }
            R.id.deleteFolder -> {
                // Remove only if the id exists
                notes.forEach {
                    db.removeNote(it.id)
                }
                db.removeFolder(folderId)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return true
            }
            R.id.editFolder -> {
                val i = Intent(this, AddEditFolderActivity::class.java)
                //i.putExtra("editNoteId", noteId)
                i.putExtra("editFolderId", folderId)
                startActivity(i)
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
        val adapter = NoteAdapter(this, notes)
        noteBoard.adapter = adapter
    }
}