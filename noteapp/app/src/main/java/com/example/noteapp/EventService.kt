package com.example.noteapp

import com.example.common.Folder
import com.example.common.Note
import retrofit2.Retrofit
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface EventService {
    @GET ("getNotesWithNoFolder")
    suspend fun getNotesWithNoFolder(@Query("sortBy") sortBy: String = "sortBy",
                                     @Query("sortMethod") sortMethod: String = "sortMethod"): List<Note>

    @POST("addNote")
    suspend fun addNote(@Query("id") id: Long,
                        @Query("title") title: String,
                        @Query("body") body: String,
                        @Query("color") color: Int,
                        @Query("tags") tags: String,
                        @Query("folderId") folderId: Int?
    )

    @POST("addFolder")
    suspend fun addFolder(@Query("id") id: Long,
                          @Query("title") title: String,
                          @Query("color") color: Int
    )

    @GET("getFolderById")
    suspend fun getFolderById(@Query("id") id:Long): Folder

    @GET("getNoteById")
    suspend fun getNoteById(@Query("id") id: Long): Note

    @GET("getDeletedNotes")
    suspend fun getDeletedNotes(): List<Note>

    @GET("getAllFolders")
    suspend fun getAllFolders(): List<Folder>

    @POST("removeNoteTemporarily")
    suspend fun removeNoteTemporarily(@Query("id") id: Long)

    @DELETE("removeNote")
    suspend fun removeNote(@Query("id") id: Long)

    @DELETE("removeFolder")
    suspend fun removeFolder(@Query("id") id: Long)

    @POST("lockNote")
    suspend fun lockNote(@Query("id") id: Long,
                         @Query("password") password: String)

    @POST("unlockNote")
    suspend fun unlockNote(@Query("id") id: Long)

    @GET("getSearchNotes")
    suspend fun getSearchNotes(@Query("criteria") criteria: String,
                               @Query("sortBy") sortBy: String = "sortBy",
                               @Query("sortMethod") sortMethod: String = "sortMethod"): List<Note>

    @GET("getNotesByFolder")
    suspend fun getNotesByFolder(@Query("id") id: Int): List<Note>

    @GET("getSearchNotesInFolder")
    suspend fun getSearchNotesInFolder(@Query("criteria") criteria: String,
                                       @Query("folderId") folderId: Int,
                                       @Query("sortBy") sortBy: String = "sortBy",
                                       @Query("sortMethod") sortMethod: String = "sortMethod"): List<Note>

    @GET("getTags")
    suspend fun getTags(@Query("id") id:Long): String

    @POST("moveToMain")
    suspend fun moveToMain(@Query("id") id: Long)

    @POST("moveToFolder")
    suspend fun moveToFolder(@Query("id") id: Long,
                             @Query("folderId") folderId: Int)
}

val Retrofit.eventService: EventService get() = this.create(EventService::class.java)