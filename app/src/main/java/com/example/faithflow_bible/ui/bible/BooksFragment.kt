package com.example.faithflow_bible.ui.bible

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.faithflow_bible.R
import com.example.faithflow_bible.data.BookInfo
import com.example.faithflow_bible.databinding.FragmentBooksBinding

/** The list of 66 books, split Old Testament / New Testament. */
class BooksFragment : Fragment() {

    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BooksViewModel
    private lateinit var adapter: BooksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[BooksViewModel::class.java]

        adapter = BooksAdapter { openChapters(it) }
        binding.booksList.adapter = adapter

        viewModel.rows.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    private fun openChapters(book: BookInfo) {
        findNavController().navigate(
            R.id.navigation_chapters,
            Bundle().apply { putInt("book", book.index) }
        )
    }

    override fun onDestroyView() {
        binding.booksList.adapter = null
        _binding = null
        super.onDestroyView()
    }
}