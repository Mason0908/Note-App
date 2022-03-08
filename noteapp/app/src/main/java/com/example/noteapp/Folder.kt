package com.example.noteapp

/**
 * @Description A data class for storing a single folder
 */
data class Folder (
    val id: Int,
    var notesId: List<Int>,
    var title: String,
    var color: Int
)