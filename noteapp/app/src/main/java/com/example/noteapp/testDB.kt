package com.example.noteapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*
import com.example.common.Note
import com.example.common.Folder

class testDB(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given
        val query2 = ("CREATE TABLE folders (\n" +
                "\tid INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \n" +
                "\ttitle TEXT, \n" +
                "\tcolor INTEGER DEFAULT 0 NOT NULL,\n" +
                "\tmodify_date DATETIME \n" +
                ");")
        db.execSQL(query2)
        val query = ("CREATE TABLE notes (\n" +
                "\tid INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\tfolder_id INTEGER,\n" +
                "\ttitle TEXT,\n" +
                "\tbody TEXT,\n" +
                "\tlocked INTEGER DEFAULT 0 NOT NULL,\n" +
                "\tpassword TEXT,\n" +
                "\tcolor INTEGER DEFAULT 0 NOT NULL,\n" +
                "\ttags TEXT,\n" +
                "\tmodify_date DATETIME,\n" +
                "\tdelete_date DATETIME,\n" +
                "FOREIGN KEY (folder_id) REFERENCES folders(id));")
        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.version = oldVersion
    }

    /**
     * @Description Adding a new note to the database
     */
    fun addNote(title: String, body: String, color: Int, tags: String?, folderId: Int?){

        val values = ContentValues()

        values.put("title", title)
        values.put("body", body)
        values.put("color", color)
        values.put("tags", tags)
        values.put("folder_id", folderId)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = Date()
        values.put("modify_date", dateFormat.format(date))

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
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = Date()
        values.put("modify_date", dateFormat.format(date))

        val db = this.writableDatabase

        db.insert(TABLE_FOLDERS_NAME, null, values)

        db.close()
    }

    /**
     * @Description Get all notes from the database
     */
    fun getAllNotes(sortBy: String = "modify_date", sortMethod: String = "DESC"): MutableList<Note> {

        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLE_NOTES_NAME ORDER BY $sortBy $sortMethod", null)

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

    fun getNotesWithNoFolder(sortBy: String = "modify_date", sortMethod: String = "DESC"): MutableList<Note> {

        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLE_NOTES_NAME WHERE folder_id is NULL ORDER BY $sortBy $sortMethod", null)

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

    fun getDeletedNotes(): MutableList<Note> {

        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLE_NOTES_NAME WHERE delete_date is NOT NULL", null)

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
    fun getAllFolders(sortBy: String = "modify_date", sortMethod: String = "DESC"): MutableList<Folder> {

        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLE_FOLDERS_NAME ORDER BY $sortBy $sortMethod", null)

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
     * @Description Get folder id of a note
     */
    fun getFolderIdOfNote(noteId: Int): Int? {
        val note = getNoteById(noteId)
        return note?.folderId ?: null
    }

    /**
     * @Description Check if a note has a folder id
     */
    fun noteHasFolder(id: Int): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NOTES_NAME WHERE id=$id and folder_id is not NULL", null)
        return cursor.count != 0
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
     * @Description Removing a note in the database by id
     */
    fun removeNote(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NOTES_NAME, "id=$id", null)
        db.close()
    }

    /**
     * @Description Temporarily removing a note(i.e., moving to recycle bin) by id
     */
    fun removeNoteTemporarily(id: Int) {
        val values = ContentValues()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = Date()
        values.put("folder_id", -1)
        values.put("delete_date", dateFormat.format(date))
        val db = this.writableDatabase
        db.update(TABLE_NOTES_NAME, values, "id=$id", null)
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
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = Date()
        values.put("modify_date", dateFormat.format(date))
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
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = Date()
        values.put("modify_date", dateFormat.format(date))
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
     * @Description Get notes in the main board by searching criteria
     */
    fun getSearchNotes(criteria: String, sortBy: String = "modify_date", sortMethod: String = "DESC"): MutableList<Note> {
        val filteredNotes = mutableListOf<Note>()
        val allNotes = getNotesWithNoFolder(sortBy, sortMethod)
        filteredNotes.addAll(allNotes.filter { note ->
            note.body.contains(
                criteria,
                true
            ) || note.title.contains(criteria, true)
        } as MutableList<Note>)
        return filteredNotes
    }

    /**
     * @Description Get notes within a folder by searching criteria
     */
    fun getSearchNotesInFolders(criteria: String, folderId: Int, sortBy: String = "modify_date", sortMethod: String = "DESC"): MutableList<Note> {
        val filteredNotes = mutableListOf<Note>()
        val allNotes = getAllFolderNotesObject(folderId, sortBy, sortMethod)
        filteredNotes.addAll(allNotes!!.filter { note ->
            note.body.contains(
                criteria,
                true
            ) || note.title.contains(criteria, true)
        } as MutableList<Note>)
        return filteredNotes
    }

    /**
     * @Description Get folders in the main board by searching criteria
     */
    fun getSearchFolders(criteria: String, sortBy: String = "modify_date", sortMethod: String = "DESC"): MutableList<Folder> {
        val filteredFolders = mutableListOf<Folder>()
        val allFolders = getAllFolders(sortBy, sortMethod)
        filteredFolders.addAll(allFolders.filter { folder ->
            folder.title.contains(criteria, true)
        } as MutableList<Folder>)
        return filteredFolders
    }

    fun hasTag(tag: String, noteId: Int): Boolean {
        val listOfTags = getNoteById(noteId)!!.tags?.split(",") ?: listOf()
        return listOfTags!!.contains(tag)
    }

    fun getTags(id: Int): String {
        return getNoteById(id)!!.tags ?: ""
    }

    /**
     * @Description Removing a note from a folder/recycle bin to another folder
     */
    fun moveNoteToFolder(noteId: Int, newFolderId: Int) {
        val values = ContentValues()
        values.put("folder_id", newFolderId)
        values.putNull("delete_date")
        val db = this.writableDatabase
        db.update(TABLE_NOTES_NAME, values, "id=$noteId", null)
        db.close()
    }

    /**
     * @Description Removing a note from a folder/recycle bin to main board
     */
    fun moveNoteToMainBoard(noteId: Int) {
        val values = ContentValues()
        values.putNull("folder_id")
        values.putNull("delete_date")
        val db = this.writableDatabase
        db.update(TABLE_NOTES_NAME, values, "id=$noteId", null)
        db.close()
    }

    /**
     * @Description Helper function to transform the
     *              current position of cursor to a note object
     */
    @SuppressLint("Range")

    fun cursorToNote(cursor: Cursor?): Note? {
        val note = Note(cursor!!.getInt(cursor.getColumnIndex("id")).toLong(),
            cursor!!.getInt(cursor.getColumnIndex("folder_id")),
            cursor!!.getString(cursor.getColumnIndex("title")),
            cursor!!.getString(cursor.getColumnIndex("body")),
            cursor!!.getInt(cursor.getColumnIndex("locked")) == 1,
            cursor!!.getString(cursor.getColumnIndex("password")),
            cursor!!.getInt(cursor.getColumnIndex("color")),
            cursor!!.getString(cursor.getColumnIndex("tags")),
            cursor!!.getString(cursor.getColumnIndex("modify_date"))
        )
        return note
    }

    /**
     * @Description Helper function to return all notes within a folder as a list of ids
     */
    @SuppressLint("Range")
    fun getAllFolderNotes(id: Int, sortBy: String = "modify_date", sortMethod: String = "DESC"): List<Int>? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT id FROM $TABLE_NOTES_NAME as notes " +
                "WHERE notes.folder_id IS $id ORDER BY $sortBy $sortMethod", null)
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
    fun getAllFolderNotesObject(id: Int, sortBy: String = "modify_date", sortMethod: String = "DESC"): MutableList<Note> {
        val allNotesId = getAllFolderNotes(id, sortBy, sortMethod)
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
        val id = cursor!!.getInt(cursor.getColumnIndex("id")).toLong()
        val notes = getAllFolderNotes(id.toInt()) // list of note IDs

        val folder = Folder(id, notes!!,
            cursor!!.getString(cursor.getColumnIndex("title")),
            cursor!!.getInt(cursor.getColumnIndex("color")),
            cursor!!.getString(cursor.getColumnIndex("modify_date"))
        )
        return folder
    }

    companion object{
        // here we have defined variables for our database

        // below is variable for database name
        private const val DATABASE_NAME = "TestDB"

        // below is the variable for database version
        private const val DATABASE_VERSION = 1

        // below is the variable for notes table name
        const val TABLE_NOTES_NAME = "notes"

        // below is the variable for folder table name
        const val TABLE_FOLDERS_NAME = "folders"
    }
}
