package com.example.flightmobileapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UrlListAdapter internal constructor(
    context: Context,
    private val listener:(Url) -> Unit
) : RecyclerView.Adapter<UrlListAdapter.UrlViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var urls = emptyList<Url>() // Cached copy of words

    fun updateEditText(view: Url) {

    }

    inner class UrlViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val urlItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrlViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return UrlViewHolder(itemView)
    }

    fun urlOnClick(view: View) {
        Log.i("check", "url clicked");
    }

    override fun onBindViewHolder(holder: UrlViewHolder, position: Int) {
        val current = urls[position]
        holder.urlItemView.text = current.url
        holder.itemView.setOnClickListener { listener(current) }
    }

    internal fun setUrls(urls: List<Url>) {
        this.urls = urls
        notifyDataSetChanged()
    }

    override fun getItemCount() = urls.size
}
