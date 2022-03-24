package com.example.noteapp

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test

class FolderDBTest {

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

    // region Folder Tests
    /**
     * The folder tests should be place before the note test
     * because note functions have associated with folders
     */

    @Test
    fun add_delete_folder() {
        Assert.assertEquals(0, db.getAllFolders().size)
        db.addFolder("f1", 1)
        Assert.assertEquals(1, db.getAllFolders().size)
        numOfFolder = db.getAllFolders()[0].id.toInt()
        db.removeFolder(numOfFolder)
        Assert.assertEquals(0, db.getAllFolders().size)
    }

    @Test
    fun has_folder() {
        Assert.assertEquals(0, db.getAllFolders().size)
        db.addFolder("f1", 1)
        Assert.assertEquals(1, db.getAllFolders().size)
        numOfFolder = db.getAllFolders()[0].id.toInt()
        Assert.assertEquals(true, db.hasFolder(numOfFolder))
        db.removeFolder(numOfFolder)
        Assert.assertEquals(0, db.getAllFolders().size)
    }

    @Test
    fun edit_folder() {
        Assert.assertEquals(0, db.getAllFolders().size)
        db.addFolder("f1", 1)
        Assert.assertEquals(1, db.getAllFolders().size)
        numOfFolder = db.getAllFolders()[0].id.toInt()
        db.editFolder(numOfFolder, "folder1")
        Assert.assertEquals("folder1", db.getFolderById(numOfFolder)?.title)
        db.removeFolder(numOfFolder)
        Assert.assertEquals(0, db.getAllFolders().size)
    }

    @Test
    fun get_search_folders() {
        Assert.assertEquals(0, db.getAllFolders().size)
        db.addFolder("f1", 1)
        Assert.assertEquals(1, db.getAllFolders().size)
        db.addFolder("f2f1", 2)
        Assert.assertEquals(2, db.getAllFolders().size)
        Assert.assertEquals(2, db.getSearchFolders("F1").size)
        Assert.assertEquals(1, db.getSearchFolders("F2f1").size)
        numOfFolder = db.getAllFolders()[0].id.toInt()
        db.removeFolder(numOfFolder++)
        db.removeFolder(numOfFolder)
        Assert.assertEquals(0, db.getAllFolders().size)
    }

    // endregion

    // region Sprint3
    @Test
    fun get_latest_folder() {
        Assert.assertEquals(0, db.getAllFolders().size)
        db.addFolder("f1", 1)
        Assert.assertEquals(1, db.getAllFolders().size)
        numOfFolder = db.getAllFolders()[0].id.toInt()
        val folder = db.getFolderById(numOfFolder)
        if (folder != null) {
            db.getLatestFolder()?.let { Assert.assertEquals(folder.id, it.id) }
        }
        db.removeFolder(numOfFolder)
        Assert.assertEquals(0, db.getAllFolders().size)
    }
    // endregion
}
