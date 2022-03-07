package com.example.noteapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * @Description class for handling all database queries
 * @author Mason Ma
 */
class DB(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given
        val query = ("CREATE TABLE notes (\n" +
                "\tid INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\tfolder_id INTEGER,\n" +
                "\ttitle TEXT,\n" +
                "\tbody TEXT,\n" +
                "\tlocked INTEGER DEFAULT 0 NOT NULL,\n" +
                "\tpassword TEXT,\n" +
                "\tcolor INTEGER DEFAULT 0 NOT NULL\n" +
                ");")
        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        if (p1 < 2){
            db.execSQL("ALTER TABLE notes ADD tags TEXT")
        }
        if (p1 < 3){
            val query = ("DROP TABLE IF EXISTS folders; \n")
            db.execSQL(query)
            val query2 = ("CREATE TABLE folders (\n" +
                    "\tid INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \n" +
                    "\ttitle TEXT, \n" +
                    "\tcolor INTEGER DEFAULT 0 NOT NULL\n" +
                    ");")
            db.execSQL(query2)
        }
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.version = oldVersion
    }

    /**
     * @Description Adding a new note to the database
     */
    fun addNote(title: String, body: String, color: Int, tags: String?, folderId: Int){

        val values = ContentValues()

        values.put("title", title)
        values.put("body", body)
        values.put("color", color)
        values.put("tags", tags)
        values.put("folderId", folderId)

        val db = this.writableDatabase

        db.insert(TABLE_NOTES_NAME, null, values)

        db.close()
    }

    /**
     * @Description Adding a new folder to the database
     */
    fun addFolder(title: String, color: Int){

        val values = ContentValues()

        values.put("title", title)
        values.put("color", color)

        val db = this.writableDatabase

        db.insert(TABLE_FOLDERS_NAME, null, values)

        db.close()
    }

    /**
     * @Description Get all notes from the database
     */
    fun getAllNotes(): MutableList<Note> {

        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLE_NOTES_NAME", null)

        cursor!!.moveToFirst()
        val allNotes = mutableListOf<Note>()
        if (cursor.count == 0){
            return allNotes
        }
        var currNote: Note?;
        currNote = cursorToNote(cursor)
        if (currNote != null){
            allNotes.add(currNote)
        }
        while (cursor.moveToNext()) {
            currNote = cursorToNote(cursor)
            if (currNote != null) {
                allNotes.add(currNote)
            }
        }
        cursor.close()
        return allNotes
    }


    /**
     * @Description Get all folders from the database
     */
    fun getAllFolders(): MutableList<Folder> {

        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLE_FOLDERS_NAME", null)

        cursor!!.moveToFirst()
        val allFolders = mutableListOf<Folder>()
        if (cursor.count == 0){
            return allFolders
        }
        var currFolder: Folder?
        currFolder = cursorToFolder(cursor)
        if (currFolder != null){
            allFolders.add(currFolder)
        }
        while (cursor.moveToNext()) {
            currFolder = cursorToFolder(cursor)
            if (currFolder != null) {
                allFolders.add(currFolder)
            }
        }
        cursor.close()
        return allFolders
    }

    /**
     * @Description Getting a note from database by id
     */
    fun getNoteById(id: Int): Note? {
        if (hasNote(id)) {
            val db = this.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM $TABLE_NOTES_NAME WHERE id=$id", null)
            cursor!!.moveToFirst()
            return cursorToNote(cursor)
        }
        return null
    }

    /**
     * @Description Getting a folder from database by id
     */
    fun getFolderById(id: Int): Folder? {
        if (hasFolder(id)) {
            val db = this.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM $TABLE_FOLDERS_NAME WHERE id=$id", null)
            cursor!!.moveToFirst()
            return cursorToFolder(cursor)
        }
        return null
    }

    /**
     * @Description Removing a note the database by id
     */
    fun removeNote(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NOTES_NAME, "id=$id", null)
        db.close()
    }

    /**
     * @Description Removing a folder the database by id
     */
    fun removeFolder(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_FOLDERS_NAME, "id=$id", null)
        db.close()
    }

    /**
     * @Description Editing a note in the database by id
     */
    fun editNote(id: Int, newTitle: String, newBody: String, newTags: String) {
        val values = ContentValues()

        values.put("title", newTitle)
        values.put("body", newBody)
        values.put("tags", newTags)
        val db = this.writableDatabase
        db.update(TABLE_NOTES_NAME, values, "id=$id", null)
        db.close()
    }

    /**
     * @Description Editing a folder in the database by id
     */
    fun editFolder(id: Int, newTitle: String) {
        val values = ContentValues()

        values.put("title", newTitle)
        val db = this.writableDatabase
        db.update(TABLE_FOLDERS_NAME, values, "id=$id", null)
        db.close()
    }

    /**
     * @Description Check if a note exist by id
     */
    fun hasNote(id: Int): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NOTES_NAME WHERE id=$id", null)

        return cursor.count == 1
    }

    /**
     * @Description Check if a folder exist by id
     */
    fun hasFolder(id: Int): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_FOLDERS_NAME WHERE id=$id", null)

        return cursor.count == 1
    }

    /**
     * @Description Lock a note by id
     */
    fun lockNote(id: Int, password: String) {
        val values = ContentValues()
        values.put("locked", 1)
        values.put("password", password)
        val db = this.writableDatabase
        db.update(TABLE_NOTES_NAME, values, "id=$id", null)
        db.close()
    }

    /**
     * @Description Unlock a note by id
     */
    fun unlockNote(id: Int) {
        val values = ContentValues()
        values.put("locked", 0)
        values.putNull("password")
        val db = this.writableDatabase
        db.update(TABLE_NOTES_NAME, values, "id=$id", null)
        db.close()
    }

    /**
     * @Description Get notes by searching criteria
     */
    fun getSearchNotes(criteria: String): MutableList<Note> {
        val filteredNotes = mutableListOf<Note>()
        val allNotes = getAllNotes()
        filteredNotes.addAll(allNotes.filter { note ->
            note.body.contains(
                criteria,
                true
            ) || note.title.contains(criteria, true)
        } as MutableList<Note>)
        return filteredNotes
    }

    /**
     * @Description Get folders by searching criteria
     */
    fun getSearchFolders(criteria: String): MutableList<Folder> {
        val filteredFolders = mutableListOf<Folder>()
        val allFolders = getAllFolders()
        filteredFolders.addAll(allFolders.filter { folder ->
            folder.title.contains(criteria, true)
        } as MutableList<Folder>)
        return filteredFolders
    }

    fun hasTag(tag: String, noteId: Int): Boolean {
        return getNoteById(noteId)!!.tags?.contains(tag) ?: false
    }

    fun getTags(id: Int): String {
        return getNoteById(id)!!.tags ?: ""
    }

    /**
     * @Description Helper function to transform the
     *              current position of cursor to a note object
     */
    @SuppressLint("Range")

    fun cursorToNote(cursor: Cursor?): Note? {
        val note = Note(cursor!!.getInt(cursor.getColumnIndex("id")),
            cursor!!.getInt(cursor.getColumnIndex("folder_id")),
            cursor!!.getString(cursor.getColumnIndex("title")),
            cursor!!.getString(cursor.getColumnIndex("body")),
            cursor!!.getInt(cursor.getColumnIndex("locked")) == 1,
            cursor!!.getString(cursor.getColumnIndex("password")),
            cursor!!.getInt(cursor.getColumnIndex("color")),
            cursor!!.getString(cursor.getColumnIndex("tags")),
        )
        return note
    }

    /**
     * @Description Helper function to return all notes within a folder as a list of ids
     */
    @SuppressLint("Range")
    fun getAllFolderNotes(id: Int): List<Int>? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT id FROM $TABLE_NOTES_NAME as notes " +
                "WHERE notes.folder_id IS $id", null)

        cursor!!.moveToFirst()
        val allNotesId = mutableListOf<Int>()
        if (cursor.count == 0) {
            return allNotesId
        }

        do {
            val currId = cursor!!.getInt(cursor.getColumnIndex("id"))
            if (cursor != null){
                allNotesId.add(currId)
            }
        } while (cursor.moveToNext())
        cursor.close()
        return allNotesId
    }

    /**
     * @Description Return all notes within a folder as a list of notes
     */
    @SuppressLint("Range")
    fun getAllFolderNotesObject(id: Int): MutableList<Note>? {
        val db = this.readableDatabase
        val allNotesId = getAllFolderNotes(id)
        var allNotes = mutableListOf<Note>()

        val notesIterator = allNotesId?.iterator()
        while (notesIterator!!.hasNext()) {
            val note = getNoteById(notesIterator.next())
            allNotes.add(note!!)
        }
        return allNotes
    }

    /**
     * @Description Helper function to transform the
     *              current position of cursor to a folder object
     */
    @SuppressLint("Range")
    fun cursorToFolder(cursor: Cursor?): Folder? {
        val id = cursor!!.getInt(cursor.getColumnIndex("id"))
        val notes = getAllFolderNotes(id) // list of note IDs

        val folder = Folder(id, notes!!,
            cursor!!.getString(cursor.getColumnIndex("title")),
            cursor!!.getInt(cursor.getColumnIndex("color")),
        )
        return folder
    }

    companion object{
        // here we have defined variables for our database

        // below is variable for database name
        private const val DATABASE_NAME = "NoteApp"

        // below is the variable for database version
        private const val DATABASE_VERSION = 3

        // below is the variable for notes table name
        const val TABLE_NOTES_NAME = "notes"

        // below is the variable for folder table name
        const val TABLE_FOLDERS_NAME = "folders"
    }
}
