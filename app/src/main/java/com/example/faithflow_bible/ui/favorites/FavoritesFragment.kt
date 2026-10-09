package com.example.faithflow_bible.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.faithflow_bible.R
import com.example.faithflow_bible.databinding.FragmentFavoritesBinding
import com.example.faithflow_bible.ui.home.RecentSavedAdapter
import com.example.faithflow_bible.ui.openBible

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

        binding.chipAll.setOnClickListener { viewModel.toggleFilter(FavoritesFilter.ALL) }
        binding.chipHighlighted.setOnClickListener { viewModel.toggleFilter(FavoritesFilter.HIGHLIGHTED) }
        binding.chipBookmarked.setOnClickListener { viewModel.toggleFilter(FavoritesFilter.SAVED) }
        binding.btnEmptyAction.setOnClickListener { openBible() }

        viewModel.ui.observe(viewLifecycleOwner) { render(it) }
    }

    override fun onStart() {
        super.onStart()
        viewModel.refresh()
    }

    private fun render(ui: FavoritesUi) {
        binding.savedSubtitle.text = "${ui.saved} saved · ${ui.highlighted} highlighted"
        adapter.submitList(ui.items)
        val empty = ui.items.isEmpty()
        binding.favoritesList.isVisible = !empty
        binding.emptyState.isVisible = empty
        binding.emptyTitle.setText(
            if (ui.filter == FavoritesFilter.HIGHLIGHTED) R.string.ff_no_highlights_title
            else R.string.ff_no_saved_title
        )
        binding.chipAll.isChecked = ui.filter == FavoritesFilter.ALL
        binding.chipHighlighted.isChecked = ui.filter == FavoritesFilter.HIGHLIGHTED
        binding.chipBookmarked.isChecked = ui.filter == FavoritesFilter.SAVED
    }

    override fun onDestroyView() {
        binding.favoritesList.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
