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
                ")")

        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        if (p1 < 2){
            db.execSQL("ALTER TABLE notes ADD tags TEXT")
        }
    }

    /**
     * @Description Adding a new note to the database
     */
    fun addNote(title: String, body: String, color: Int){

        val values = ContentValues()

        values.put("title", title)
        values.put("body", body)
        values.put("color", color)

        val db = this.writableDatabase

        db.insert(TABLE_NAME, null, values)

        db.close()
    }

    /**
     * @Description Get all notes from the database
     */
    fun getAllNotes(): MutableList<Note> {

        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

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
     * @Description Getting a note from database by id
     */
    fun getNoteById(id: Int): Note? {
        if (hasNote(id)) {
            val db = this.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE id=$id", null)
            cursor!!.moveToFirst()
            return cursorToNote(cursor)
        }
        return null
    }
    /**
     * @Description Removing a note the database by id
     */
    fun removeNote(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "id=$id", null)
        db.close()
    }

    /**
     * @Description Editing a note in the database by id
     */
    fun editNote(id: Int, newTitle: String, newBody: String) {
        val values = ContentValues()

        values.put("title", newTitle)
        values.put("body", newBody)
        val db = this.writableDatabase
        db.update(TABLE_NAME, values, "id=$id", null)
        db.close()
    }

    /**
     * @Description Check if a note exist by id
     */
    fun hasNote(id: Int): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE id=$id", null)

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
        db.update(TABLE_NAME, values, "id=$id", null)
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
        db.update(TABLE_NAME, values, "id=$id", null)
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
     * @Description Helper function to transform the
     *              current position of cursor to a note object
     */
    @SuppressLint("Range")
    fun cursorToNote(cursor: Cursor?): Note?{
        val note = Note(cursor!!.getInt(cursor.getColumnIndex("id")),
            cursor!!.getInt(cursor.getColumnIndex("folder_id")),
            cursor!!.getString(cursor.getColumnIndex("title")),
            cursor!!.getString(cursor.getColumnIndex("body")),
            cursor!!.getInt(cursor.getColumnIndex("locked")) == 1,
            cursor!!.getString(cursor.getColumnIndex("password")),
            cursor!!.getInt(cursor.getColumnIndex("color")),
        )
        return note
    }

    companion object{
        // here we have defined variables for our database

        // below is variable for database name
        private val DATABASE_NAME = "NoteApp"

        // below is the variable for database version
        private val DATABASE_VERSION = 2

        // below is the variable for table name
        val TABLE_NAME = "notes"
    }
}
