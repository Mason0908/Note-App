package com.example.noteapp

data class Note (
    val id: Int,
    var folderId: Int?,
    var title: String,
    var body: String
    )