/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.example.common

//import com.google.gson.Gson
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
//
@Serializable
data class Note (
    val id: Long,
    var folderId: Int?,
    var title: String,
    var body: String,
    var isLocked: Boolean,
    var password: String?,
    var color: Int,
    var tags: String?,
    var modify_date: String? = null,
    var delete_date: String? = null,
    var color_heading: String? = null,
    var color_body: String? = null,
    var font: String? = null
){
    override fun equals(other: Any?): Boolean {
        return (other is Note) && (id == other.id) && (folderId == other.folderId) &&
                (title == other.title) && (body == other.body) && (isLocked == other.isLocked) &&
                (password == other.password) && (color == other.color) && (tags == other.tags) &&
                (color_body == other.color_body) && (color_heading == color_heading) &&
                (font == other.font)
    }
}

fun Note.toJsonString():String {
    return Json.encodeToString(this)
}

fun List<Note>.toJsonString(): String{
    return Json.encodeToString(this)
}

