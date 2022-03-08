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
class DBTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val db = testDB(appContext, null)
    private var numOfNote = 0
    private var numOfFolder = 0

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

    /**
     * The folder tests should be place before the note test
     * because note functions have associated with folders
     */

    @Test
    fun add_delete_folder() {
        assertEquals(0, db.getAllFolders().size)
        db.addFolder("f1", 1)
        assertEquals(1, db.getAllFolders().size)
        numOfFolder = db.getAllFolders()[0].id
        db.removeFolder(numOfFolder)
        assertEquals(0, db.getAllFolders().size)
    }

    @Test
    fun has_folder() {
        assertEquals(0, db.getAllFolders().size)
        db.addFolder("f1", 1)
        assertEquals(1, db.getAllFolders().size)
        numOfFolder = db.getAllFolders()[0].id
        assertEquals(true, db.hasFolder(numOfFolder))
        db.removeFolder(numOfFolder)
        assertEquals(0, db.getAllFolders().size)
    }

    @Test
    fun edit_folder() {
        assertEquals(0, db.getAllFolders().size)
        db.addFolder("f1", 1)
        assertEquals(1, db.getAllFolders().size)
        numOfFolder = db.getAllFolders()[0].id
        db.editFolder(numOfFolder, "folder1")
        assertEquals("folder1", db.getFolderById(numOfFolder)?.title)
        db.removeFolder(numOfFolder)
        assertEquals(0, db.getAllFolders().size)
    }

    @Test
    fun get_search_folders() {
        assertEquals(0, db.getAllFolders().size)
        db.addFolder("f1", 1)
        assertEquals(1, db.getAllFolders().size)
        db.addFolder("f2f1", 2)
        assertEquals(2, db.getAllFolders().size)
        assertEquals(2, db.getSearchFolders("F1").size)
        assertEquals(1, db.getSearchFolders("F2f1").size)
        numOfFolder = db.getAllFolders()[0].id
        db.removeFolder(numOfFolder++)
        db.removeFolder(numOfFolder)
        assertEquals(0, db.getAllFolders().size)
    }

    /**
     * Test adding and deleting notes functionality
     * and ensure the title and body as desired.
     */
    @Test
    fun add_delete_notes() {
        assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id
        db.removeNote(numOfNote)
        assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun edit_note() {
        assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id
        db.editNote(numOfNote, "new title", "new body", "add tag")
        val curr = db.getAllNotes()[0]
        assertEquals("new title", curr.title)
        assertEquals("new body", curr.body)
        assertEquals("add tag", curr.tags)
        db.removeNote(numOfNote)
        assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun get_note_by_id() {
        assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id
        db.addNote("title2", "body2", 2, "tag", 1)
        assertEquals(2, db.getAllNotes().size)
        val getNoteByID = db.getNoteById(numOfNote)
        assertEquals("title", getNoteByID?.title)
        assertEquals("body", getNoteByID?.body)
        assertEquals(null, getNoteByID?.tags)
        val getNoteByID2 = db.getNoteById(numOfNote + 1)
        assertEquals("title2", getNoteByID2?.title)
        assertEquals("body2", getNoteByID2?.body)
        assertEquals("tag", getNoteByID2?.tags)
        db.removeNote(numOfNote++)
        db.removeNote(numOfNote)
        assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun has_note() {
        assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id
        assertEquals(true, db.hasNote(numOfNote))
        db.removeNote(numOfNote)
        assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun lock_note() {
        assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id
        db.lockNote(numOfNote, "password")
        assertEquals("password", db.getNoteById(numOfNote)?.password)
        assertEquals(true, db.getNoteById(numOfNote)?.isLocked)
        db.removeNote(numOfNote)
        assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun unlock_note() {
        assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        assertEquals(1, db.getAllNotes().size)
        db.lockNote(numOfNote, "password")
        numOfNote = db.getAllNotes()[0].id
        db.unlockNote(numOfNote)
        assertEquals(false, db.getNoteById(numOfNote)?.isLocked)
        assertEquals(null, db.getNoteById(numOfNote)?.password)
        db.removeNote(numOfNote)
        assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun search_note() {
        assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        db.addNote("TITLE", "nAh", 2, null, 1)
        assertEquals(2, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id
        val searchTitle = db.getSearchNotes("tITLe", 1)
        assertEquals(2, searchTitle.size)
        assertEquals("title", searchTitle[0].title)
        assertEquals("TITLE", searchTitle[1].title)
        val searchBody1 = db.getSearchNotes("body", 1)
        assertEquals(1, searchBody1.size)
        assertEquals("body", searchBody1[0].body)
        val searchBody2 = db.getSearchNotes("Nah", 1)
        assertEquals(1, searchBody2.size)
        assertEquals("nAh", searchBody2[0].body)
        db.removeNote(numOfNote++)
        db.removeNote(numOfNote)
        assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun has_tag() {
        assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, "has tag", 1)
        assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id
        assertEquals(false, db.hasTag("hh", numOfNote))
        assertEquals(true, db.hasTag("has tag", numOfNote))
        db.removeNote(numOfNote)
        assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun get_tag() {
        assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, "has tag", 1)
        assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id
        assertEquals("has tag", db.getTags(numOfNote))
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
        numOfFolder = db.getAllFolders()[0].id
        db.addNote("title", "body", 1, null, numOfFolder)
        db.addNote("title2", "body2", 2, null, numOfFolder)
        numOfNote = db.getAllNotes()[0].id
        assertEquals(2, db.getAllNotes().size)
        assertEquals(2, db.getAllFolderNotes(numOfFolder)?.size)
        db.removeNote(numOfNote++)
        db.removeNote(numOfNote)
        assertEquals(0, db.getAllNotes().size)
        db.removeFolder(numOfFolder)
        assertEquals(0, db.getAllFolders().size)
    }
}