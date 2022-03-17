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

class FolderListAdapter internal constructor(context: Context?, folders: MutableList<Folder>, noteToBeMoved: Int, hasMainBoard: Boolean):
    RecyclerView.Adapter<FolderListAdapter.ViewHolder?>() {
    private val inflater: LayoutInflater
    private val folders: List<Folder>
    private val noteToBeMoved: Int = noteToBeMoved
    private val hasMainBoard: Boolean = hasMainBoard


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.folder_simple_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        if (hasMainBoard) {
            var fTitle: String = "Main Board"
            if (i != 0) {
                fTitle = folders[i - 1].title
                viewHolder.folderCard.setCardBackgroundColor(ContextCompat.getColor(viewHolder.folderCard.context, folders[i - 1].color))
            }
            viewHolder.fTitle.text = fTitle
        } else {
            var fTitle: String = folders[i].title
            viewHolder.fTitle.text = fTitle
            viewHolder.folderCard.setCardBackgroundColor(ContextCompat.getColor(viewHolder.folderCard.context, folders[i].color))
        }

    }

    override fun getItemCount(): Int {
        if (hasMainBoard) {
            return folders.size + 1
        } else {
            return folders.size
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var fTitle: TextView = itemView.findViewById(R.id.fTitle)
        val folderCard: CardView = itemView.findViewById(R.id.folderCard)

        init {
            itemView.setOnClickListener { v ->
                val db = DB(v.context, null)
                if (hasMainBoard) {
                    if (adapterPosition == 0) {
                        db.moveNoteToMainBoard(noteToBeMoved)
                    } else {
                        db.moveNoteToFolder(noteToBeMoved, folders[adapterPosition-1].id)
                    }
                    val i = Intent(v.context, MainActivity::class.java)
                    v.context.startActivity(i)
                } else {
                    db.moveNoteToFolder(noteToBeMoved, folders[adapterPosition].id)
                    val i = Intent(v.context, MainActivity::class.java)
                    v.context.startActivity(i)
                }
            }
        }
    }

    init {
        inflater = LayoutInflater.from(context)
        this.folders = folders
    }
}
