package com.example.faithflow_bible.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.faithflow_bible.R
import com.example.faithflow_bible.data.BibleRepository
import com.example.faithflow_bible.data.Verse
import com.example.faithflow_bible.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
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

        binding.statSavedCount.text = viewModel.savedCount.toString()
        binding.statHighlightsCount.text = viewModel.highlightCount.toString()
        binding.statChaptersCount.text = viewModel.chaptersRead.toString()

        binding.recentList.adapter = RecentSavedAdapter { openInBible(it) }
            .also { it.submitList(viewModel.recentSaved) }

        binding.btnReadContext.setOnClickListener { currentVerse?.let { openInBible(it) } }
        binding.btnSettings.setOnClickListener {
            Snackbar.make(binding.root, R.string.ff_settings_soon, Snackbar.LENGTH_SHORT).show()
        }
    }

    // Re-checked every time the screen comes back, so the verse rolls over on the hour.
    override fun onStart() {
        super.onStart()
        showVerseOfTheHour(viewModel.verseOfTheHour())
    }

    private fun showVerseOfTheHour(verse: Verse) {
        currentVerse = verse
        binding.verseText.text = "\u201C${verse.text}\u201D"
        binding.verseReference.text = "${verse.reference} (${verse.translation})"
        binding.verseTag.text = verse.tag
        binding.btnShare.setOnClickListener { share(verse) }
    }

    /** Opens the Bible tab on the verse's chapter, scrolled to the verse. */
    private fun openInBible(verse: Verse) {
        val ref = BibleRepository.get(requireContext()).parseReference(verse.reference)
        val args = Bundle()
        if (ref != null) {
            args.putInt("book", ref.bookIndex)
            args.putInt("chapter", ref.chapter)
            args.putInt("verse", ref.verse)
        }
        val options = NavOptions.Builder()
            .setPopUpTo(R.id.navigation_home, false)
            .setLaunchSingleTop(true)
            .build()
        findNavController().navigate(R.id.navigation_bible, args, options)
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
