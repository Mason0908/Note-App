package com.example.noteapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import kotlin.random.Random

/**
 * @Description Adapter for Recycle View
 */

class Adapter internal constructor(context: Context?, notes: MutableList<Note>, folders: MutableList<Folder>):
    RecyclerView.Adapter<Adapter.ViewHolder?>() {
    private val inflater: LayoutInflater
    private val notes: List<Note>
    private val folders: List<Folder>
    private val allInList: MutableList<Any> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.note_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        if (allInList[i] is Note) {
            val startIndex = folders.size
            val title: String = notes[i-startIndex].title
            val id: Int = notes[i-startIndex].id
            viewHolder.noteCard.setCardBackgroundColor(ContextCompat.getColor(viewHolder.noteCard.context, notes[i-startIndex].color))
            viewHolder.nTitle.text = title
            viewHolder.nID.text = java.lang.String.valueOf(id)
            viewHolder.nDate.text = notes[i-startIndex].modify_date
            viewHolder.lock.isVisible = notes[i-startIndex].isLocked
            viewHolder.imageNote.isVisible = !notes[i-startIndex].isLocked
            viewHolder.imageFolder.isVisible = false
        } else if (allInList[i] is Folder) {
            val title: String = folders[i].title
            val id: Int = folders[i].id
            viewHolder.noteCard.setCardBackgroundColor(ContextCompat.getColor(viewHolder.noteCard.context, folders[i].color))
            viewHolder.nTitle.text = title
            viewHolder.nID.text = java.lang.String.valueOf(id)
            println("modi date: " + folders[i].modify_date)
            viewHolder.nDate.text = folders[i].modify_date
            viewHolder.lock.isVisible = false
            viewHolder.imageNote.isVisible = false
            viewHolder.imageFolder.isVisible = true
        }
    }

    override fun getItemCount(): Int {
        return allInList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nTitle: TextView = itemView.findViewById(R.id.nTitle)
        var nID: TextView = itemView.findViewById(R.id.listId)
        var nDate: TextView = itemView.findViewById(R.id.nDate)
        val lock: ImageView = itemView.findViewById(R.id.imageLock)
        val noteCard: CardView = itemView.findViewById(R.id.noteCard)
        val imageNote: ImageView = itemView.findViewById(R.id.imageNote)
        val imageFolder: ImageView = itemView.findViewById(R.id.imageFolder)

        init {
            itemView.setOnClickListener { v ->
                val i = Intent(v.context, ViewNoteActivity::class.java)
                val i2 = Intent(v.context, ViewFolderActivity::class.java)
                val i3 = Intent(v.context, ViewDeletedActivity::class.java)
                if (allInList[adapterPosition] is Note) {
                    val startIndex = folders.size
                    i.putExtra("displayNoteId", notes[adapterPosition-startIndex].id)
                    v.context.startActivity(i)
                } else if (allInList[adapterPosition] is Folder) {
                    val f: Folder = allInList[adapterPosition] as Folder
                    if (f.id == -1) {
                        println("f.id is -1")
                        v.context.startActivity(i3)
                    } else {
                        i2.putExtra("displayFolderId", folders[adapterPosition].id)
                        v.context.startActivity(i2)
                    }
                }
            }
        }
    }

    init {
        inflater = LayoutInflater.from(context)
        this.notes = notes
        this.folders = folders
        this.allInList.addAll(folders)
        this.allInList.addAll(notes)
    }
}