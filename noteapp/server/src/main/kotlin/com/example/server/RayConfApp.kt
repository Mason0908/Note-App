package com.example.server

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import com.example.common.Note
import com.example.common.Folder
import com.example.common.toJsonString
import io.ktor.routing.*

val dataSource = DataSource()

fun Application.main() {
    routing {
        get("/") {
            call.respond("Hello World!!!!!!")
        }
        get("getNotesWithNoFolder"){
            call.respondText(contentType = ContentType.Application.Json) {
                dataSource.getNotesWithNoFolder().toJsonString()
            }
        }
        post("addNote"){
            val id = call.request.queryParameters["id"]?.toLong()
            val title = call.request.queryParameters["title"]
            val body = call.request.queryParameters["body"]
            val color = call.request.queryParameters["color"]?.toInt()
            val tags = call.request.queryParameters["tags"]
            val folderId = call.request.queryParameters["folderId"]?.toInt()

            if (id != null && title != null && body != null && color != null && tags != null){
                dataSource.addNote(id, title, body, color, tags, folderId)
                call.respondText("note added to database")
            }
            else{
                call.respond(HttpStatusCode.BadRequest, "Missing param")
            }
        }

        post("addFolder"){
            val id = call.request.queryParameters["id"]?.toLong()
            val title = call.request.queryParameters["title"]
            val color = call.request.queryParameters["color"]?.toInt()
            if (id != null && title != null && color != null){
                dataSource.addFolder(id, title, color)
                call.respondText("folder added to database")
            }
            else{
                call.respond(HttpStatusCode.BadRequest, "Missing param")
            }
        }

        get("getFolderById"){
            val id = call.request.queryParameters["id"]?.toLong()
            if (id != null){
                call.respondText(contentType = ContentType.Application.Json) {
                    dataSource.getFolderById(id).toJsonString()
                }
            }
        }

        get("getNoteById"){
            val id = call.request.queryParameters["id"]?.toLong()
            if (id != null){
                call.respondText(contentType = ContentType.Application.Json) {
                    dataSource.getNoteById(id).toJsonString()
                }
            }
        }

        get("getDeletedNotes"){
            call.respondText(contentType = ContentType.Application.Json) {
                dataSource.getDeletedNotes().toJsonString()
            }
        }

        get("getAllFolders"){
            call.respondText(contentType = ContentType.Application.Json) {
                dataSource.getAllFolders().toJsonString()
            }
        }

        post("removeNoteTemporarily"){
            val id = call.request.queryParameters["id"]?.toLong()
            if (id != null){
                dataSource.removeNoteTemporarily(id)
                call.respondText("temporarily removed note")
            }
            else{
                call.respond(HttpStatusCode.BadRequest, "Missing param")
            }
        }

        delete("removeNote"){
            val id = call.request.queryParameters["id"]?.toLong()
            if (id != null){
                dataSource.removeNote(id)
                call.respondText("permanently removed note")
            }
            else{
                call.respond(HttpStatusCode.BadRequest, "Missing param")
            }
        }

        delete("removeFolder"){
            val id = call.request.queryParameters["id"]?.toLong()
            if (id != null){
                dataSource.removeFolder(id)
                call.respondText("permanently removed folder")
            }
            else{
                call.respond(HttpStatusCode.BadRequest, "Missing param")
            }
        }

        post("lockNote"){
            val id = call.request.queryParameters["id"]?.toLong()
            val password = call.request.queryParameters["password"]?.toString()
            if (id != null && password != null){
                dataSource.lockNote(id, password)
                call.respondText("note locked")
            }
            else{
                call.respond(HttpStatusCode.BadRequest, "Missing param")
            }
        }

        post("unlockNote"){
            val id = call.request.queryParameters["id"]?.toLong()
            if (id != null){
                dataSource.unlockNote(id)
                call.respondText("note unlocked")
            }
            else{
                call.respond(HttpStatusCode.BadRequest, "Missing param")
            }
        }

        get ("getSearchNotes"){
            val criteria = call.request.queryParameters["criteria"]
            val sortBy = call.request.queryParameters["sortBy"]
            val sortMethod = call.request.queryParameters["sortMethod"]
            if (criteria != null){
                if (sortBy != null && sortMethod != null){
                    call.respondText(contentType = ContentType.Application.Json) {
                        dataSource.getSearchNotes(criteria, sortBy, sortMethod).toJsonString()
                    }

                }
                else{
                    call.respondText(contentType = ContentType.Application.Json) {
                        dataSource.getSearchNotes(criteria).toJsonString()
                    }
                }
            }
            else{
                call.respond(HttpStatusCode.BadRequest, "Missing param")
            }
        }

        get("getNotesByFolder"){
            val id = call.request.queryParameters["id"]?.toInt()
            if (id != null){
                val notes = dataSource.getNotesByFolder(id)
                call.respondText(contentType = ContentType.Application.Json) {
                    notes.toJsonString()
                }
            }
            else{
                call.respond(HttpStatusCode.BadRequest, "Missing param")
            }
        }

        get ("getSearchNotesInFolder"){
            val criteria = call.request.queryParameters["criteria"]
            val folderId = call.request.queryParameters["folderId"]?.toInt()
            val sortBy = call.request.queryParameters["sortBy"]
            val sortMethod = call.request.queryParameters["sortMethod"]
            if (criteria != null && folderId != null){
                if (sortBy != null && sortMethod != null){
                    call.respondText(contentType = ContentType.Application.Json) {
                        dataSource.getSearchNotesInFolder(criteria, folderId, sortBy, sortMethod).toJsonString()
                    }
                }
                else{
                    call.respondText(contentType = ContentType.Application.Json) {
                        dataSource.getSearchNotesInFolder(criteria,folderId).toJsonString()
                    }
                }
            }
            else{
                call.respond(HttpStatusCode.BadRequest, "Missing param")
            }
        }

        get("getTags"){
            val id = call.request.queryParameters["id"]?.toLong()
            if (id != null){
                val tags = dataSource.getTags(id)
                call.respond(tags)
            }
            else{
                call.respond(HttpStatusCode.BadRequest, "Missing param")
            }
        }

        post("moveToMain"){
            val id = call.request.queryParameters["id"]?.toLong()
            if (id != null){
                dataSource.moveToMain(id)
                call.respondText("note Moved to Main Board")
            }
            else{
                call.respond(HttpStatusCode.BadRequest, "Missing param")
            }
        }

        post("moveToFolder"){
            val id = call.request.queryParameters["id"]?.toLong()
            val folderId = call.request.queryParameters["folderId"]?.toInt()
            if (id != null && folderId != null){
                dataSource.moveToFolder(id, folderId)
                call.respondText("note Moved to Folder")
            }
            else{
                call.respond(HttpStatusCode.BadRequest, "Missing param")
            }
        }
    }
}
