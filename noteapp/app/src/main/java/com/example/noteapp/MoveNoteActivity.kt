package com.example.noteapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common.Note
import com.example.common.Folder

class MoveNoteActivity : AppCompatActivity() {
    private lateinit var folderList: RecyclerView
    private var noteId: Int = -1
    private var dstFolderId: Int? = null
    private val db = DB(this, null)
    private lateinit var folders: MutableList<Folder>
    private var isInMainBoard: Boolean = false
    private var isRestore: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.folder_list)

        // the action bar with current note title and delete
        val actionBar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(actionBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        folderList = findViewById(R.id.folderList)
        folders = db.getAllFolders()

        // Retrieve the note if exist
        val i = intent
        isRestore = i.getBooleanExtra("isRestore", false)
        noteId = i.getIntExtra("noteToBeMoved", -1)

        val noteHasFolder = db.noteHasFolder(noteId)
        if (noteHasFolder) {
            val noteFolderId: Int? = db.getFolderIdOfNote(noteId)
            if (noteFolderId!! >= 0) {
                folders.removeIf { folder ->
                    folder.id.toInt() == noteFolderId
                }
            }
        } else {
            isInMainBoard = true
        }

        displayFolderList()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                if (isRestore) {
                    val i = Intent(this, ViewDeletedNoteActivity::class.java)
                    i.putExtra("deletedNoteId", noteId)
                    startActivity(i)
                    finish()
                    return true
                } else {
                    val i = Intent(this, ViewNoteActivity::class.java)
                    i.putExtra("displayNoteId", noteId)
                    startActivity(i)
                    finish()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }



    private fun displayFolderList() {
        folderList.layoutManager = LinearLayoutManager(this)
        val adapter = FolderListAdapter(this, folders, noteId, !isInMainBoard)
        folderList.adapter = adapter
    }
}