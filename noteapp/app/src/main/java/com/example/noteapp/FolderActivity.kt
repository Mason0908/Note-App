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

/**
 * @Description Folder screen
 */
class FolderActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var folderBoard: RecyclerView
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var folders: MutableList<Folder>
    private lateinit var adapter: FolderAdapter
    private val db = DB(this, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folders)
        folders = db.getAllFolders()
        val testFolders = db.getAllFolders()

        // Get reference for folder list
        folderBoard = findViewById(R.id.folderBoard)

        // Tying with the adapter
        folderBoard.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        adapter = FolderAdapter(this, folders)
        folderBoard.adapter = adapter

        val actionBar: Toolbar = findViewById(R.id.toolbar)
        // showing the back button in action bar
        setSupportActionBar(actionBar)
        supportActionBar!!.title = "My Folders"

        // Get reference for add button
        btnAdd = findViewById(R.id.btnAddFolder)
        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddFolderActivity::class.java))
            finish()
        }

        // Display list if exists
        if (folders.size > 0){
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
/*
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
            }
            displayList()
        }
    }
*/
    /**
     * @Description Set up adapter for recycle view
     * @author Mason
     * @return void
     */
    private fun displayList() {
        folderBoard.layoutManager = LinearLayoutManager(this)
        val adapter = FolderAdapter(this, folders)
        folderBoard.adapter = adapter
    }

}