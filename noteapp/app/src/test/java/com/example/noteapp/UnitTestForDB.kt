package com.example.noteapp

import android.os.Build.VERSION_CODES.LOLLIPOP
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class,
    sdk = intArrayOf(LOLLIPOP),
    packageName = "com.example.noteapp")
class DBTest() {
    private lateinit var db: DB

    @Before
    fun setup() {
        db = DB(RuntimeEnvironment.application, null)
    }
    @After
    fun tearDown() {
    }

    @Test
    @Throws(Exception::class)
    fun testDbInsertion() {

        // When
//        db.addNote("test1", "TEST ONE", 1, null)
//
//        // Then
//        assertEquals(db.getAllNotes().size, 1)
    }
}

