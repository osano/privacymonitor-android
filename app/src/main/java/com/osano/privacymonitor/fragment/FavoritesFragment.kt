package com.osano.privacymonitor.fragment

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import com.osano.privacymonitor.adapter.FavoritesRecyclerViewAdapter
import com.osano.privacymonitor.R
import com.osano.privacymonitor.data.AppDatabase
import com.osano.privacymonitor.data.FavoritesRepository
import com.osano.privacymonitor.models.FavoriteURL
import com.osano.privacymonitor.util.toast

class FavoritesFragment : Fragment() {

    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var favoritesRepository: FavoritesRepository
    private lateinit var adapter: FavoritesRecyclerViewAdapter
    private lateinit var noResultsTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.favoritesRecyclerView)
        adapter = FavoritesRecyclerViewAdapter(ArrayList(favoritesRepository.getUrls()), listener)
        recyclerView.adapter = adapter

        noResultsTextView = view.findViewById(R.id.noResultsTextView)

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                favoritesRepository.delete(adapter.getUrlAt(viewHolder.adapterPosition))
                adapter.removeUrlAt(position)

                context?.toast(getString(R.string.toastUrlDeleted))
                updateUI()
            }
        }).attachToRecyclerView(recyclerView)

        updateUI()

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        favoritesRepository = FavoritesRepository.getInstance(AppDatabase.getInstance(context.applicationContext).favoriteURLDao())

        if (context is OnListFragmentInteractionListener) {
            listener = context
        }
        else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun updateUI() {
        noResultsTextView.visibility = when(adapter.itemCount) {
            0 -> View.VISIBLE
            else -> View.INVISIBLE
        }
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: FavoriteURL?)
    }
}
