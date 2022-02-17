package com.example.noteapp

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * @Description Adapter for Recycle View
 */
class Adapter internal constructor(context: Context?, notes: MutableList<Note>):
    RecyclerView.Adapter<Adapter.ViewHolder?>() {
    private val inflater: LayoutInflater
    private val notes: List<Note>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.note_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val title: String = notes[i].title
        val id: Int = notes[i].id
        viewHolder.nTitle.text = title
        viewHolder.nID.text = java.lang.String.valueOf(id)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nTitle: TextView
        var nID: TextView

        init {
            nTitle = itemView.findViewById(R.id.nTitle)
            nID = itemView.findViewById(R.id.listId)
            itemView.setOnClickListener { v ->
                val i = Intent(v.context, ViewNoteActivity::class.java)
                i.putExtra("displayId", notes[adapterPosition].id)
                v.context.startActivity(i)
            }
        }
    }

    init {
        inflater = LayoutInflater.from(context)
        this.notes = notes
    }
}