package com.neo.notekeeperkotlin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class NoteRecyclerAdapter(private val context : Context, private val notes : List<NoteInfo>) :
    RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder>() {

    // prop not passed to our custom Adapter class constructor
    private val layoutInflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemView = layoutInflater.inflate(R.layout.item_note_list, parent,
            false)      // prevents the inflatedView from permanently attaching to parent View
        return ViewHolder(itemView)
    }

//    override fun getItemCount(): Int {
//        return notes.size
//    }


    // shorter implementation of fun above via kt
    override fun getItemCount() = notes.size



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notes[position]

        holder.textCourse.text = note.course?.title
        holder.textTitle.text = note.title
        holder.notePosition = position
    }


    // custom ViewHolder class, inner allows this class to access prop of outer class(Adapter class)
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        // gets ref to the textCourse tv
        val textCourse = itemView.findViewById<TextView>(R.id.textCourse)
        val textTitle = itemView.findViewById<TextView>(R.id.textTitle)

        // helps to keep track of note(via pos) assoc with ViewHolder in focus, for retrieving notes
        var notePosition = 0

        init {
            itemView.setOnClickListener{
                var intent = Intent(context, NoteActivity::class.java)
                intent.putExtra(NOTE_POSITION, notePosition)
                context.startActivity(intent)
            }
        }
    }
}