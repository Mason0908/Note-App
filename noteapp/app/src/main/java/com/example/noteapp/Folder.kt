package com.example.noteapp

/**
 * @Description A data class for storing a single folder
 */

/**
 * List of todos from Yiran mostly all regarding folders
 * TODO: perhaps add modified date to folder data object as well
 */

data class Folder (
    val id: Int,
    var notesId: List<Int>,
    var title: String,
    var color: Int
)