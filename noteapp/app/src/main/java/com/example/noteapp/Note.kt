package com.example.noteapp

/**
 * @Description A data class for storing a single note
 */
data class Note (
    val id: Int,
    var folderId: Int?,
    var title: String,
    var body: String,
    var isLocked: Boolean,
    var password: String?
    )