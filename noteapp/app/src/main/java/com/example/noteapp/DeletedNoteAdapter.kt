package com.example.noteapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class DeletedNoteAdapter internal constructor(context: Context?, notes: MutableList<Note>):
    RecyclerView.Adapter<DeletedNoteAdapter.ViewHolder?>() {
    private val inflater: LayoutInflater
    private val notes: List<Note>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.note_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val title: String = notes[i].title
        val id: Int = notes[i].id
        viewHolder.noteCard.setCardBackgroundColor(ContextCompat.getColor(viewHolder.noteCard.context, notes[i].color))
        viewHolder.nTitle.text = title
        viewHolder.nID.text = java.lang.String.valueOf(id)
        viewHolder.nDate.text = ""
        viewHolder.lock.isVisible = false
        viewHolder.imageNote.isVisible = true
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
                val i = Intent(v.context, ViewDeletedNoteActivity::class.java)
                i.putExtra("deletedNoteId", notes[adapterPosition].id)
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
