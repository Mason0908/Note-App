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
import com.example.common.Note
import com.example.common.Folder

/**
 * @Description Adapter for Recycle View
 */

class NoteAdapter internal constructor(context: Context?, notes: MutableList<Note>):
    RecyclerView.Adapter<NoteAdapter.ViewHolder?>() {
    private val inflater: LayoutInflater
    private val notes: List<Note>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.note_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val title: String = notes[i].title
        val id: Int = notes[i].id.toInt()
        viewHolder.noteCard.setCardBackgroundColor(notes[i].color)
        viewHolder.nTitle.text = title
        viewHolder.nID.text = java.lang.String.valueOf(id)
        viewHolder.nDate.text = notes[i].modify_date
        viewHolder.lock.isVisible = notes[i].isLocked
        viewHolder.imageNote.isVisible = !notes[i].isLocked
        viewHolder.imageFolder.isVisible = false
    }

    override fun getItemCount(): Int {
        return notes.size
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
                val db = DB(v.context, null)
                val i = Intent(v.context, ViewNoteActivity::class.java)
                i.putExtra("displayNoteId", notes[adapterPosition].id.toInt())
                //i.putExtra("displayNoteFolderId", db.getFolderIdOfNote(notes[adapterPosition].id))
                v.context.startActivity(i)
            }
        }
    }

    init {
        inflater = LayoutInflater.from(context)
        this.notes = notes
    }
}
