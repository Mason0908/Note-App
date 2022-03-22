@file:Suppress("EXPERIMENTAL_API_USAGE")
package com.example.common
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
//
@Serializable
data class AgendaEntry(
    val id: Long,
    val title: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val description: String,
    val speaker: String,
    val votes: Long = 0,
    val updated: Long = System.currentTimeMillis()
)


fun AgendaEntry.toJsonString(): String =
    Json.encodeToString(this)

fun List<AgendaEntry>.toJsonString(): String =
    Json.encodeToString(this)
