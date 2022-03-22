@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.example.common


//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.Json
//import kotlinx.serialization.list
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
//
@Serializable
data class Folder (
    val id: Long,
    var notesId: List<Int>?,
    var title: String,
    var color: Int,
    var modify_date: String?
)


fun Folder.toJsonString():String {
    return Json.encodeToString(this)
}

fun List<Folder>.toJsonString(): String{
    return Json.encodeToString(this)
}