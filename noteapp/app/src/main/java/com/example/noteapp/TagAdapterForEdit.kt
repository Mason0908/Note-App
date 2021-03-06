package com.example.noteapp

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TagAdapterForEdit internal constructor(context: Context?, tags: String, tagBoard: RecyclerView, activity: AddEditNoteActivity):
    RecyclerView.Adapter<TagAdapterForEdit.ViewHolder?>() {
    private val inflater: LayoutInflater
    private val activity: AddEditNoteActivity
    private var tags: String = tags
    private val tagsList: MutableList<String>
    private val tagBoard: RecyclerView

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

        init {
            itemView.setOnClickListener { v ->
                val db = DB(v.context, null)
                val builder: AlertDialog.Builder = AlertDialog.Builder(v.context)
                builder.setTitle("Remove this tag?")
                builder.setPositiveButton("Yes", null)
                builder.setNegativeButton("Cancel", null)
                val dialog = builder.create()
                dialog.setOnShowListener {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                        tagsList.remove(tName.text.toString())
                        tags = tagsList.joinToString(",")
                        activity.tags = tags
                        tagBoard.layoutManager = LinearLayoutManager(v.context, RecyclerView.HORIZONTAL, false)
                        val adapter = TagAdapterForEdit(v.context, tags, tagBoard, activity)
                        tagBoard.adapter = adapter
                        dialog.dismiss()
                    }
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
                        dialog.cancel()
                    }
                }
                dialog.show()
            }
        }
    }

    init {
        inflater = LayoutInflater.from(context)
        this.tagsList = tags.split(",") as MutableList<String>
        this.tagBoard = tagBoard
        this.activity = activity
    }
}