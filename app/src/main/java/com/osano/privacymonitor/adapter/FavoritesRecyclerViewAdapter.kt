package com.osano.privacymonitor.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.osano.privacymonitor.R
import com.osano.privacymonitor.fragment.FavoritesFragment.OnListFragmentInteractionListener
import com.osano.privacymonitor.models.FavoriteURL

class FavoritesRecyclerViewAdapter(
    private var urls: ArrayList<FavoriteURL>,
    private val listener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<FavoritesRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as FavoriteURL
            listener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_favoriteurl, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = urls[position]
        holder.urlTextView.text = item.url

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = urls.size

    fun getUrlAt(position: Int): FavoriteURL = urls.get(position)

    fun removeUrlAt(position: Int) {
        urls.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val urlTextView: TextView = mView.findViewById(R.id.urlTextView)

        override fun toString(): String {
            return super.toString() + " '" + urlTextView.text + "'"
        }
    }
}
