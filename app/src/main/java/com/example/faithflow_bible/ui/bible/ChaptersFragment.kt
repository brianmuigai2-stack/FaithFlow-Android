package com.example.faithflow_bible.ui.bible

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.faithflow_bible.R
import com.example.faithflow_bible.databinding.FragmentChaptersBinding

/** Chapter grid for one book. */
class ChaptersFragment : Fragment() {

    private var _binding: FragmentChaptersBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChaptersViewModel
    private lateinit var adapter: ChaptersAdapter
    private var bookIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChaptersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ChaptersViewModel::class.java]

        adapter = ChaptersAdapter { openChapter(it) }
        binding.chaptersList.adapter = adapter

        bookIndex = arguments?.getInt("book", 0) ?: 0
        viewModel.load(bookIndex)

        viewModel.ui.observe(viewLifecycleOwner) { ui ->
            ui.book?.let { binding.chaptersBookTitle.text = it.name }
            binding.chaptersSubtitle.text = ui.subtitle
            adapter.submitList(ui.chapters)
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun openChapter(chapter: Int) {
        findNavController().navigate(
            R.id.navigation_reader,
            Bundle().apply {
                putInt("book", bookIndex)
                putInt("chapter", chapter)
                putInt("verse", 0)
            }
        )
    }

    override fun onDestroyView() {
        binding.chaptersList.adapter = null
        _binding = null
        super.onDestroyView()
    }
}