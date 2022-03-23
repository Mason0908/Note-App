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
    var password: String?,
    var color: Int,
    var tags: String?,
    var modify_date: String? = null,
    var color_heading: String? = null,
    var color_body: String? = null,
    var font: String? = null
    )