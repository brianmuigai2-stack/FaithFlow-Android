package com.example.faithflow_bible.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.faithflow_bible.R
import com.example.faithflow_bible.data.BibleRepository
import com.example.faithflow_bible.data.Verse
import com.example.faithflow_bible.databinding.FragmentHomeBinding
import com.example.faithflow_bible.ui.openBible
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var recentAdapter: RecentSavedAdapter
    private var currentVerse: Verse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        recentAdapter = RecentSavedAdapter { openBible(it.bookIndex, it.chapter, it.verse) }
        binding.recentList.adapter = recentAdapter

        viewModel.ui.observe(viewLifecycleOwner) { ui ->
            binding.statSavedCount.text = ui.saved.toString()
            binding.statHighlightsCount.text = ui.highlighted.toString()
            binding.statChaptersCount.text = ui.chapters.toString()
            recentAdapter.submitList(ui.recent)
            binding.recentList.isVisible = ui.recent.isNotEmpty()
            binding.recentEmpty.isVisible = ui.recent.isEmpty()
        }

        binding.btnReadContext.setOnClickListener { currentVerse?.let { openInBible(it) } }
        binding.btnSettings.setOnClickListener {
            Snackbar.make(binding.root, R.string.ff_settings_soon, Snackbar.LENGTH_SHORT).show()
        }
    }

    // Re-checked every time the screen comes back: the verse rolls over on the hour,
    // and the stats pick up anything saved or highlighted in the meantime.
    override fun onStart() {
        super.onStart()
        showVerseOfTheHour(viewModel.verseOfTheHour())
        viewModel.refresh()
    }

    private fun showVerseOfTheHour(verse: Verse) {
        currentVerse = verse
        binding.verseText.text = "\u201C${verse.text}\u201D"
        binding.verseReference.text = "${verse.reference} (${verse.translation})"
        binding.verseTag.text = verse.tag
        binding.btnShare.setOnClickListener { share(verse) }
    }

    private fun openInBible(verse: Verse) {
        val ref = BibleRepository.get(requireContext()).parseReference(verse.reference)
        if (ref != null) openBible(ref.bookIndex, ref.chapter, ref.verse) else openBible()
    }

    private fun share(verse: Verse) {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "\u201C${verse.text}\u201D\n\u2014 ${verse.reference} (${verse.translation})\n\nShared from FaithFlow"
            )
        }
        startActivity(Intent.createChooser(send, null))
    }

    override fun onDestroyView() {
        binding.recentList.adapter = null
        _binding = null
        super.onDestroyView()
    }
}