package com.example.faithflow_bible.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.faithflow_bible.R
import com.example.faithflow_bible.databinding.FragmentFavoritesBinding
import com.example.faithflow_bible.ui.home.RecentSavedAdapter
import com.example.faithflow_bible.ui.openBible
import com.google.android.material.card.MaterialCardView

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var adapter: RecentSavedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[FavoritesViewModel::class.java]

        adapter = RecentSavedAdapter { openBible(it.bookIndex, it.chapter, it.verse) }
        binding.favoritesList.adapter = adapter

        binding.cardHighlighted.setOnClickListener {
            viewModel.toggleFilter(FavoritesFilter.HIGHLIGHTED)
        }
        binding.cardBookmarked.setOnClickListener {
            viewModel.toggleFilter(FavoritesFilter.SAVED)
        }
        binding.cardChapters.setOnClickListener {
            viewModel.toggleFilter(FavoritesFilter.ALL)
        }
        binding.btnEmptyAction.setOnClickListener { openBible() }

        viewModel.ui.observe(viewLifecycleOwner) { render(it) }
    }

    // Reload whenever we come back, so new highlights and saves show up.
    override fun onStart() {
        super.onStart()
        viewModel.refresh()
    }

    private fun render(ui: FavoritesUi) {
        binding.countHighlighted.text = ui.highlighted.toString()
        binding.countBookmarked.text = ui.saved.toString()
        binding.countChapters.text = ui.chapters.toString()

        adapter.submitList(ui.items)
        val empty = ui.items.isEmpty()
        binding.favoritesList.isVisible = !empty
        binding.emptyState.isVisible = empty
        binding.emptyTitle.setText(
            if (ui.filter == FavoritesFilter.HIGHLIGHTED) R.string.ff_no_highlights_title
            else R.string.ff_no_saved_title
        )

        markSelected(binding.cardHighlighted, ui.filter == FavoritesFilter.HIGHLIGHTED)
        markSelected(binding.cardBookmarked, ui.filter == FavoritesFilter.SAVED)
    }

    private fun markSelected(card: MaterialCardView, selected: Boolean) {
        val density = resources.displayMetrics.density
        card.strokeWidth = ((if (selected) 2 else 1) * density).toInt()
        card.strokeColor = ContextCompat.getColor(
            requireContext(), if (selected) R.color.ff_primary else R.color.ff_outline
        )
    }

    override fun onDestroyView() {
        binding.favoritesList.adapter = null
        _binding = null
        super.onDestroyView()
    }
}