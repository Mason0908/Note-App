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
 * @Description Folder Adapter for Recycle View
 */

class FolderAdapter internal constructor(context: Context?, folders: MutableList<Folder>):
    RecyclerView.Adapter<FolderAdapter.ViewHolder?>() {
    private val inflater: LayoutInflater
    private val folders: List<Folder>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.folder_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val title: String = folders[i].title
        val id: Int = folders[i].id
        val notesid: List<Int> = folders[i].notesId
        viewHolder.folderCard.setCardBackgroundColor(ContextCompat.getColor(viewHolder.folderCard.context, folders[i].color))
        viewHolder.fTitle.text = title
        //viewHolder.nID.text = java.lang.String.valueOf(id)
        //viewHolder.lock.isVisible = notes[i].isLocked
        //viewHolder.nTitle.isVisible = !notes[i].isLocked
        //viewHolder.imageNote.isVisible = !notes[i].isLocked
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var fTitle: TextView = itemView.findViewById(R.id.fTitle)
        // var nID: TextView = itemView.findViewById(R.id.listId)
        val folderCard: CardView = itemView.findViewById(R.id.folderCard)

        init {
            itemView.setOnClickListener { v ->
                val i = Intent(v.context, MainActivity::class.java)
                i.putExtra("folderId", folders[adapterPosition].id)
                // println("folder ID:" + folders[adapterPosition].id)
                // i.putExtra("notesListId", folders[adapterPosition].notesId)
                // the above does not work for now (need parcelable data class)
                v.context.startActivity(i)
            }
        }
    }

    init {
        inflater = LayoutInflater.from(context)
        this.folders = folders
    }
}