package com.example.noteapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * @Description Home screen
 */
class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var noteBoard: RecyclerView
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var btnSort: FloatingActionButton
    private var ascending: Boolean = true
    private lateinit var notes: MutableList<Note>
    private lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notes = (this.application as Model).getAllNotes()

        // Get reference for note list
        noteBoard = findViewById(R.id.noteBoard)

        adapter = Adapter(this, notes)
        noteBoard.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        noteBoard.adapter = adapter;

        val actionBar: Toolbar = findViewById(R.id.toolbar)
        // showing the back button in action bar
        setSupportActionBar(actionBar)
        supportActionBar!!.title = "Note Book"

        // Get reference for add button
        btnAdd = findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
            finish()
        }

        // initialize sort button
        btnSort = findViewById(R.id.btnSort)
        btnSort.setOnClickListener{
            sortData(ascending)
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
            notes = (this.application as Model).getSearchNotes(newText)
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

    /**
     * @Description Display all notes in the database
     * @author Mason
     * @return void
     */
    private fun displayList() {
        noteBoard.setLayoutManager(LinearLayoutManager(this))
        val adapter = Adapter(this, notes)
        noteBoard.setAdapter(adapter)
    }

    private fun sortData(asc: Boolean) {
        if (asc) {
            notes.sortBy { it.title }
        } else {
            notes.sortByDescending { it.title }
        }
        startActivity(Intent(this, MainActivity::class.java))
    }

}