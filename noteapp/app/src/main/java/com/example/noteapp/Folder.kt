package com.example.noteapp

/**
 * @Description A data class for storing a single folder
 */

/**
 * List of todos from Yiran mostly all regarding folders
 * TODO: revamp MainActivity to be able to contain both folders and notes
 * TODO: they is no way of deleting a folder
 * TODO: perhaps add modified date to folder data object as well
 * TODO: there is no "back" button just yet to go from within a folder back to main
 * TODO: there might be a bug related to accessing locked notes within a folder when you lock (rlly weird)
 */

data class Folder (
    val id: Int,
    var notesId: List<Int>,
    var title: String,
    var color: Int
)