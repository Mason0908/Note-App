package com.example.server


import com.google.appengine.api.datastore.*
import com.example.common.Note
import com.example.common.Folder
import java.text.SimpleDateFormat
import java.util.*

private const val NoteEntryName = "Note"
private const val FolderEntryName = "Folder"



private fun Note.toEntity() = Entity(NoteEntryName, id).apply {
    setProperty("id", id)
    setProperty("folderId", folderId)
    setProperty("title", title)
    setProperty("body", body)
    setProperty("isLocked", when(isLocked){
        true -> 1
        else -> 0
    })
    setProperty("password", password)
    setProperty("color", color)
    setProperty("tags", tags)
    setProperty("modify_date", modify_date)
    setProperty("delete_date", delete_date)
    setProperty("color_heading", when(color_heading){
        null -> "#000000"
        else -> color_heading
    })
    setProperty("color_body", when(color_body){
        null -> "#000000"
        else -> color_body
    })
    setProperty("font", when(font){
        null -> "Arial"
        else -> font
    })
}

private fun Entity.toNote() = Note(
    id = key.id,
    folderId = when(getProperty("folderId")) {
        null -> null
        else -> getProperty("folderId").toString().toInt()},
    title = getProperty("title").toString(),
    body = getProperty("body").toString(),
    isLocked = when(getProperty("isLocked").toString().toInt()){
        0 -> false
        else -> true
    },
    password = when(getProperty("password")) {
        null -> null
        else -> getProperty("password").toString()},
    color = getProperty("color").toString().toInt(),
    tags = getProperty("tags").toString(),
    modify_date = getProperty("modify_date").toString(),
    delete_date = when(getProperty("delete_date")) {
        null -> null
        else -> getProperty("delete_date").toString()},
    color_heading = getProperty("color_heading").toString(),
    color_body = getProperty("color_body").toString(),
    font = getProperty("font").toString()
)

private fun Entity.toFolder() = Folder(
    id = key.id,
    notesId = null,
    title = getProperty("title").toString(),
    color = getProperty("color").toString().toInt(),
    modify_date = getProperty("modify_date").toString()
)


class DataSource {
    private val dataStore = DatastoreServiceFactory.getDatastoreService()

    fun addNote(id: Long, title: String, body: String, color: Int, tags: String?, folderId: Int?){
        val note = Entity(NoteEntryName, id)
        note.setProperty("id", id)
        note.setProperty("folderId", folderId)
        note.setProperty("title", title)
        note.setProperty("body", body)
        if (note.getProperty("isLocked") != null){
            note.setProperty("isLocked", note.getProperty("isLocked").toString().toInt())
        }
        else{
            note.setProperty("isLocked", 0)
        }

        note.setProperty("password", when(note.getProperty("password")){
            null -> null
            else -> note.getProperty("password").toString()
        })
        note.setProperty("color", color)
        note.setProperty("tags", tags)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        dateFormat.timeZone = TimeZone.getTimeZone("America/Toronto")
        val date = Date()
        note.setProperty("modify_date", dateFormat.format(date))
        note.setProperty("delete_date", when(note.getProperty("delete_date")){
            null -> null
            else -> note.getProperty("delete_date").toString()
        })
        note.setProperty("color_heading", when(note.getProperty("color_heading")){
            null -> "#000000"
            else -> note.getProperty("color_heading")
        })
        note.setProperty("color_body", when(note.getProperty("color_body")){
            null -> "#000000"
            else -> note.getProperty("color_body")
        })
        note.setProperty("font", when(note.getProperty("font")){
            null -> "Arial"
            else -> note.getProperty("font")
        })
        dataStore.put(note)
    }

    fun addFolder(id: Long, title: String, color: Int){
        val folder = Entity(FolderEntryName, id)
        folder.setProperty("id", id)
        folder.setProperty("title", title)
        folder.setProperty("color", color)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        dateFormat.timeZone = TimeZone.getTimeZone("America/Toronto")
        val date = Date()
        folder.setProperty("modify_date", dateFormat.format(date))
        dataStore.put(folder)
    }

    fun getNotesWithNoFolder(sortBy: String = "modify_date", sortMethod: String = "DESC"): List<Note> {
        val query = Query(NoteEntryName)
            .addSort(sortBy, when(sortMethod) {
                "DESC" -> Query.SortDirection.DESCENDING
                else -> Query.SortDirection.ASCENDING
            })
        val prep = dataStore.prepare(query)
        val result = prep.asList(FetchOptions.Builder.withDefaults()).map {it.toNote()}
        val noFolder = result.filter { it.folderId == null }
        return noFolder
    }

    fun getNoteById(id: Long): Note {
        val query = Query(NoteEntryName)
            .setFilter(Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id))
        val prep = dataStore.prepare(query)
        return prep.asList(FetchOptions.Builder.withDefaults())[0].toNote()
    }

    fun getFolderById(id: Long): Folder {
        val query = Query(FolderEntryName)
            .setFilter(Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id))
        val prep = dataStore.prepare(query)
        return prep.asList(FetchOptions.Builder.withDefaults())[0].toFolder()
    }

    fun getDeletedNotes() : List<Note> {
        val query = Query(NoteEntryName)
        val prep = dataStore.prepare(query)
        return prep.asList(FetchOptions.Builder.withDefaults()).map {it.toNote()} .filter { it.delete_date != null }
    }

    fun getAllFolders() : List<Folder>{
        val query = Query(FolderEntryName)
        val prep = dataStore.prepare(query)
        return prep.asList(FetchOptions.Builder.withDefaults()).map {it.toFolder()}
    }

    fun removeNoteTemporarily(id: Long) {
        val curr = getNoteById(id)
        val note = curr.toEntity()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        dateFormat.timeZone = TimeZone.getTimeZone("America/Toronto")
        val date = Date()
        note.setProperty("delete_date", dateFormat.format(date))
        note.setProperty("folderId", -1)
        dataStore.put(note)
    }

    fun removeNote(id: Long){
        val note = Entity(NoteEntryName, id).key
        dataStore.delete(note)
    }

    fun removeFolder(id: Long){
        val folder = Entity(FolderEntryName, id).key
        dataStore.delete(folder)
    }

    fun lockNote(id: Long, password: String){
        val curr = getNoteById(id)
        val note = curr.toEntity()
        note.setProperty("isLocked", 1)
        note.setProperty("password", password)
        dataStore.put(note)
    }

    fun unlockNote(id: Long){
        val curr = getNoteById(id)
        val note = curr.toEntity()
        note.setProperty("isLocked", 0)
        note.setProperty("password", null)
        dataStore.put(note)
    }

    fun getSearchNotes(criteria: String, sortBy: String = "modify_date", sortMethod: String = "DESC"): List<Note> {
        val notes = getNotesWithNoFolder(sortBy, sortMethod)
        val searchNotes = notes.filter { note ->
            note.body.contains(
                criteria,
                true
            ) || note.title.contains(criteria, true)
        }
        return searchNotes
    }

    fun getNotesByFolder(folderId: Int, sortBy: String = "modify_date", sortMethod: String = "DESC"): List<Note> {
        val query = Query(NoteEntryName)
            .addSort(sortBy, when(sortMethod) {
                "DESC" -> Query.SortDirection.DESCENDING
                else -> Query.SortDirection.ASCENDING
            })
        val prep = dataStore.prepare(query)
        val result = prep.asList(FetchOptions.Builder.withDefaults()).map {it.toNote()}
        val withFolder = result.filter { it.folderId == folderId }
        return withFolder
    }

    fun getSearchNotesInFolder(criteria: String, folderId: Int, sortBy: String = "modify_date", sortMethod: String = "DESC"): List<Note>{
        val notesInFolder = getNotesByFolder(folderId, sortBy, sortMethod)
        val searchNotes = notesInFolder.filter { note ->
            note.body.contains(
                criteria,
                true
            ) || note.title.contains(criteria, true)
        }
        return searchNotes
    }

    fun getTags(id: Long): String{
        return getNoteById(id).tags ?: ""
    }

    fun moveToMain(id: Long){
        val curr = getNoteById(id)
        val note = curr.toEntity()
        note.setProperty("folderId", null)
        note.setProperty("delete_date", null)
        dataStore.put(note)
    }

    fun moveToFolder(id: Long, dest: Int){
        val curr = getNoteById(id)
        val note = curr.toEntity()
        note.setProperty("folderId", dest)
        note.setProperty("delete_date", null)
        dataStore.put(note)

    }

    fun getAllNotes(): List<Note>{
        val query = Query(NoteEntryName)
        val prep = dataStore.prepare(query)
        return prep.asList(FetchOptions.Builder.withDefaults()).map {it.toNote()}
    }

    fun editNoteSetting(id: Long, field: String, newValue: String){
        val curr = getNoteById(id)
        val note = curr.toEntity()
        note.setProperty(field, newValue)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        dateFormat.timeZone = TimeZone.getTimeZone("America/Toronto")
        val date = Date()
        note.setProperty("modify_date", dateFormat.format(date))
        dataStore.put(note)
    }

    fun getNoteSetting(id: Long, field: String) : String?{
        val note = getNoteById(id)
        return when(field){
            "color_heading" -> note.color_heading
            "color_body" -> note.color_body
            "font" -> note.font
            else -> null
        }
    }
}