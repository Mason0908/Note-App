package com.example.noteapp

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * To run tests, always delete the current notes stored in database
 * To avoid conflicts of database, this test is testing against testDB
 * Which is identical to the user database DB
 * @see testDB
 */
@RunWith(AndroidJUnit4::class)
class CombineDBTest {

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val db = testDB(appContext, null)
    private var numOfNote = 0
    private var numOfFolder = 0

    // region Basic Setup
    /**
     * @Description double check if the context created as desired
     */
    @Test
    fun useAppContext() {
        // Context of the app under test.
        assertEquals("com.example.noteapp", appContext.packageName)
    }

    /**
     * the database initially is empty
     */
    @Test
    fun initial_zero() {
        assertEquals(0, db.getAllNotes().size)
        assertEquals(0, db.getAllFolders().size)
    }

    // endregion

    // region Combination
    @Test
    fun all_notes_with_no_folder() {
        assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, null)
        assertEquals(1, db.getAllNotes().size)
        db.addNote("title2", "body2", 1, null, null)
        assertEquals(2, db.getAllNotes().size)
        assertEquals(2, db.getNotesWithNoFolder().size)
        numOfNote = db.getAllNotes()[0].id.toInt()
        db.removeNote(numOfNote++)
        db.removeNote(numOfNote)
        assertEquals(0, db.getAllNotes().size)
    }

    /**
     * This is the combination of folder and notes
     * Note that folderId is a foreign key in notes
     */
    @Test
    fun get_notes_in_folder() {
        assertEquals(0, db.getAllFolders().size)
        db.addFolder("f1", 1)
        assertEquals(1, db.getAllFolders().size)
        assertEquals(0, db.getAllNotes().size)
        numOfFolder = db.getAllFolders()[0].id.toInt()
        db.addNote("title", "body", 1, null, numOfFolder)
        db.addNote("title2", "body2", 2, null, numOfFolder)
        numOfNote = db.getAllNotes()[0].id.toInt()
        assertEquals(2, db.getAllNotes().size)
        assertEquals(2, db.getAllFolderNotes(numOfFolder)?.size)
        db.removeNote(numOfNote++)
        db.removeNote(numOfNote)
        assertEquals(0, db.getAllNotes().size)
        db.removeFolder(numOfFolder)
        assertEquals(0, db.getAllFolders().size)
    }

    @Test
    fun search_notes_in_folder() {
        assertEquals(0, db.getAllFolders().size)
        db.addFolder("f1", 1)
        assertEquals(1, db.getAllFolders().size)
        assertEquals(0, db.getAllNotes().size)
        numOfFolder = db.getAllFolders()[0].id.toInt()
        db.addNote("title", "body", 1, null, numOfFolder)
        db.addNote("title2", "body2", 2, null, numOfFolder)
        numOfNote = db.getAllNotes()[0].id.toInt()
        assertEquals(0, db.getSearchNotesInFolders("TT", numOfFolder).size)
        assertEquals(2, db.getSearchNotesInFolders("TITLE", numOfFolder).size)
        assertEquals(1, db.getSearchNotesInFolders("BOdy2", numOfFolder).size)
        db.removeNote(numOfNote++)
        db.removeNote(numOfNote)
        assertEquals(0, db.getAllNotes().size)
        db.removeFolder(numOfFolder)
        assertEquals(0, db.getAllFolders().size)
    }
    // endregion

    // region Sprint 3
    @Test
    fun move_note_to_folder() {
        assertEquals(0, db.getAllFolders().size)
        db.addFolder("f1", 1)
        assertEquals(1, db.getAllFolders().size)
        assertEquals(0, db.getAllNotes().size)
        numOfFolder = db.getAllFolders()[0].id.toInt()
        db.addNote("title", "body", 1, null, numOfFolder)
        numOfNote = db.getAllNotes()[0].id.toInt()
        db.removeNoteTemporarily(numOfNote)
        db.moveNoteToFolder(numOfNote, numOfFolder)
        assertEquals(numOfFolder, db.getNoteById(numOfNote)?.folderId)
        db.removeNote(numOfNote)
        assertEquals(0, db.getAllNotes().size)
        db.removeFolder(numOfFolder)
        assertEquals(0, db.getAllFolders().size)
    }
    // endregion
}