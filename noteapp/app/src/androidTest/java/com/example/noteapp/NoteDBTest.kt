package com.example.noteapp

import androidx.test.platform.app.InstrumentationRegistry
import com.example.common.Note
import org.junit.Assert
import org.junit.Test

class NoteDBTest {
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
        Assert.assertEquals("com.example.noteapp", appContext.packageName)
    }

    /**
     * the database initially is empty
     */
    @Test
    fun initial_zero() {
        Assert.assertEquals(0, db.getAllNotes().size)
        Assert.assertEquals(0, db.getAllFolders().size)
    }

    // endregion

    // region Note Tests

    /**
     * Notes tests
     */

    /**
     * Test adding and deleting notes functionality
     * and ensure the title and body as desired.
     */
    @Test
    fun add_delete_notes() {
        Assert.assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        Assert.assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id.toInt()
        db.removeNote(numOfNote)
        Assert.assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun edit_note() {
        Assert.assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        Assert.assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id.toInt()
        db.editNote(numOfNote, "new title", "new body", "add tag")
        val curr = db.getAllNotes()[0]
        Assert.assertEquals("new title", curr.title)
        Assert.assertEquals("new body", curr.body)
        Assert.assertEquals("add tag", curr.tags)
        db.removeNote(numOfNote)
        Assert.assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun get_note_by_id() {
        Assert.assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        Assert.assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id.toInt()
        db.addNote("title2", "body2", 2, "tag", 1)
        Assert.assertEquals(2, db.getAllNotes().size)
        val getNoteByID = db.getNoteById(numOfNote)
        Assert.assertEquals("title", getNoteByID?.title)
        Assert.assertEquals("body", getNoteByID?.body)
        Assert.assertEquals(null, getNoteByID?.tags)
        val getNoteByID2 = db.getNoteById(numOfNote + 1)
        Assert.assertEquals("title2", getNoteByID2?.title)
        Assert.assertEquals("body2", getNoteByID2?.body)
        Assert.assertEquals("tag", getNoteByID2?.tags)
        db.removeNote(numOfNote++)
        db.removeNote(numOfNote)
        Assert.assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun has_note() {
        Assert.assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        Assert.assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id.toInt()
        Assert.assertEquals(true, db.hasNote(numOfNote))
        db.removeNote(numOfNote)
        Assert.assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun lock_note() {
        Assert.assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        Assert.assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id.toInt()
        db.lockNote(numOfNote, "password")
        Assert.assertEquals("password", db.getNoteById(numOfNote)?.password)
        Assert.assertEquals(true, db.getNoteById(numOfNote)?.isLocked)
        db.removeNote(numOfNote)
        Assert.assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun unlock_note() {
        Assert.assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        Assert.assertEquals(1, db.getAllNotes().size)
        db.lockNote(numOfNote, "password")
        numOfNote = db.getAllNotes()[0].id.toInt()
        db.unlockNote(numOfNote)
        Assert.assertEquals(false, db.getNoteById(numOfNote)?.isLocked)
        Assert.assertEquals(null, db.getNoteById(numOfNote)?.password)
        db.removeNote(numOfNote)
        Assert.assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun search_note() {
        Assert.assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, null)
        db.addNote("TITLE", "nAh", 2, null, null)
        Assert.assertEquals(2, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id.toInt()
        val searchTitle = db.getSearchNotes("title")
        Assert.assertEquals(2, searchTitle.size)
        Assert.assertEquals("title", searchTitle[0].title)
        Assert.assertEquals("TITLE", searchTitle[1].title)
        val searchBody1 = db.getSearchNotes("body")
        Assert.assertEquals(1, searchBody1.size)
        Assert.assertEquals("body", searchBody1[0].body)
        val searchBody2 = db.getSearchNotes("Nah")
        Assert.assertEquals(1, searchBody2.size)
        Assert.assertEquals("nAh", searchBody2[0].body)
        db.removeNote(numOfNote++)
        db.removeNote(numOfNote)
        Assert.assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun has_tag() {
        Assert.assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, "has tag", 1)
        Assert.assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id.toInt()
        Assert.assertEquals(false, db.hasTag("hh", numOfNote))
        Assert.assertEquals(true, db.hasTag("has tag", numOfNote))
        db.removeNote(numOfNote)
        Assert.assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun get_tag() {
        Assert.assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, "has tag", 1)
        Assert.assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id.toInt()
        Assert.assertEquals("has tag", db.getTags(numOfNote))
        db.removeNote(numOfNote)
        Assert.assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun get_folder_id_of_note() {
        Assert.assertEquals(0, db.getAllFolders().size)
        db.addFolder("f1", 1)
        Assert.assertEquals(1, db.getAllFolders().size)
        numOfFolder = db.getAllFolders()[0].id.toInt()
        db.addNote("title", "body", 1, null, numOfFolder)
        Assert.assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id.toInt()
        Assert.assertEquals(numOfFolder, db.getFolderIdOfNote(numOfNote))
        db.removeNote(numOfNote)
        db.removeFolder(numOfFolder)
        Assert.assertEquals(0, db.getAllFolders().size)
    }

    @Test
    fun note_has_folder_bool() {
        for (i in 0 .. db.getAllNotes().size - 1) {
            db.removeNote(db.getAllNotes()[i].id.toInt())
        }
        for (i in 0 .. db.getAllFolders().size - 1) {
            db.removeFolder(db.getAllFolders()[i].id.toInt())
        }
        Assert.assertEquals(0, db.getAllFolders().size)
        db.addFolder("f1", 1)
        Assert.assertEquals(1, db.getAllFolders().size)
        numOfFolder = db.getAllFolders()[0].id.toInt()
        db.addNote("title", "body", 1, null, numOfFolder)
        Assert.assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id.toInt()
        Assert.assertEquals(true, db.noteHasFolder(numOfNote))
        db.removeNote(numOfNote)
        db.removeFolder(numOfFolder)
        Assert.assertEquals(0, db.getAllFolders().size)
    }

    //endregion

    // region Sprint 3
    @Test
    fun remove_note_temporarily() {
        Assert.assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        Assert.assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id.toInt()
        db.removeNoteTemporarily(numOfNote)
        Assert.assertEquals(-1, db.getNoteById(numOfNote)?.folderId)
        db.removeNote(numOfNote)
        Assert.assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun move_to_main_board() {
        Assert.assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        Assert.assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id.toInt()
        db.moveNoteToMainBoard(numOfNote)
        // note that null is 0 in testDB ?
        Assert.assertEquals(null, db.getNoteById(numOfNote)?.folderId)
        db.removeNote(numOfNote)
        Assert.assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun edit_note_settings() {
        Assert.assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        Assert.assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id.toInt()
        db.editNoteSettings(numOfNote, "title", "this is a new title")
        Assert.assertEquals("this is a new title", db.getNoteById(numOfNote)?.title)
        db.editNoteSettings(numOfNote, "body", "this is a new body")
        Assert.assertEquals("this is a new body", db.getNoteById(numOfNote)?.body)
        db.removeNote(numOfNote)
        Assert.assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun get_latest_note() {
        Assert.assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        Assert.assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id.toInt()
        val note = db.getNoteById(numOfNote)
        Assert.assertEquals(note, db.getLatestNote())
        db.removeNote(numOfNote)
        Assert.assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun get_delete_notes() {
        Assert.assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        Assert.assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id.toInt()
        db.removeNoteTemporarily(numOfNote)
        Assert.assertEquals(1, db.getDeletedNotes().size)
        db.removeNote(numOfNote)
        Assert.assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun get_heading_color() {
        Assert.assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        Assert.assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id.toInt()
        db.editNoteSettings(numOfNote, "color_heading", "red")
        Assert.assertEquals("red", db.getHeadingColorOfNote(numOfNote))
        db.removeNote(numOfNote)
        Assert.assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun get_body_color() {
        Assert.assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        Assert.assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id.toInt()
        db.editNoteSettings(numOfNote, "color_body", "red")
        Assert.assertEquals("red", db.getBodyColorOfNote(numOfNote))
        db.removeNote(numOfNote)
        Assert.assertEquals(0, db.getAllNotes().size)
    }

    @Test
    fun get_note_font() {
        Assert.assertEquals(0, db.getAllNotes().size)
        db.addNote("title", "body", 1, null, 1)
        Assert.assertEquals(1, db.getAllNotes().size)
        numOfNote = db.getAllNotes()[0].id.toInt()
        Assert.assertEquals("Arial", db.getFontOfNote(numOfNote))
        db.removeNote(numOfNote)
        Assert.assertEquals(0, db.getAllNotes().size)
    }
    // endregion
}
