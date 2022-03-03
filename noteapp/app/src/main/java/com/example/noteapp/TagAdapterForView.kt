package com.example.noteapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TagAdapterForView internal constructor(context: Context?, tags: String):
    RecyclerView.Adapter<TagAdapterForView.ViewHolder?>() {
    private val inflater: LayoutInflater
    private val tagsList: MutableList<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.tag_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val name: String = tagsList[i]
        viewHolder.tName.text = name
    }

    override fun getItemCount(): Int {
        return tagsList.size-1
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tName: TextView = itemView.findViewById(R.id.tagName)
    }

    init {
        inflater = LayoutInflater.from(context)
        this.tagsList = tags.split(",") as MutableList<String>
    }
}