package com.example.faithflow_bible.ui.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.faithflow_bible.data.ReaderPrefs
import com.example.faithflow_bible.databinding.FragmentReaderBinding

class ReaderFragment : Fragment() {

    private var _binding: FragmentReaderBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ReaderViewModel
    private lateinit var prefs: ReaderPrefs
    private lateinit var adapter: ChapterAdapter

    private val textSizes = floatArrayOf(16f, 19f, 22f)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ReaderViewModel::class.java]
        prefs = ReaderPrefs(requireContext())

        val args = arguments
        viewModel.start(
            args?.getInt("book", 0) ?: 0,
            args?.getInt("chapter", 1) ?: 1,
            args?.getInt("verse", 0) ?: 0
        )

        adapter = ChapterAdapter(textSizes[prefs.textSizeIndex]) { verse ->
            VerseActionsSheet.newInstance(
                viewModel.bookIndex, viewModel.bookName, viewModel.chapter, verse.number, verse.text
            ).show(childFragmentManager, "verse_actions")
        }
        binding.chapterList.adapter = adapter
        binding.chapterList.itemAnimator = null

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.btnPrev.setOnClickListener { viewModel.previous() }
        binding.btnNext.setOnClickListener { viewModel.next() }
        binding.btnTextSize.setOnClickListener { showTextSizeMenu(it) }
        binding.translationPill.setOnClickListener { showTranslationMenu(it) }

        childFragmentManager.setFragmentResultListener(
            VerseActionsSheet.RESULT_KEY, viewLifecycleOwner
        ) { _, _ -> viewModel.refreshMarks() }

        viewModel.state.observe(viewLifecycleOwner) { render(it) }
    }

    private fun render(state: ReaderState) {
        binding.bookTitle.text = state.bookName
        binding.translationLabel.text = state.translation.code
        binding.chapterSubtitle.text = "Chapter ${viewModel.chapter}"
        binding.chapterIndicator.text = "Ch. ${viewModel.chapter} of ${state.totalChapters}"
        setEnabled(binding.btnPrev, state.hasPrevious)
        setEnabled(binding.btnNext, state.hasNext)

        // Save last-read position
        prefs.lastReadBook = viewModel.bookIndex
        prefs.lastReadChapter = viewModel.chapter

        adapter.submitList(state.rows) {
            val row = state.scrollToRow
            if (row >= 0 && _binding != null) {
                val lm = binding.chapterList.layoutManager as LinearLayoutManager
                if (row == 0) lm.scrollToPositionWithOffset(0, 0)
                else lm.scrollToPositionWithOffset(row, (96 * resources.displayMetrics.density).toInt())
            }
        }
    }

    private fun setEnabled(button: View, enabled: Boolean) {
        button.isEnabled = enabled
        button.alpha = if (enabled) 1f else 0.3f
    }

    private fun showTextSizeMenu(anchor: View) {
        val popup = PopupMenu(requireContext(), anchor)
        listOf("Small", "Medium", "Large").forEachIndexed { i, label ->
            popup.menu.add(0, i, i, label)
        }
        popup.menu.setGroupCheckable(0, true, true)
        popup.menu.getItem(prefs.textSizeIndex).isChecked = true
        popup.setOnMenuItemClickListener { item ->
            prefs.textSizeIndex = item.itemId
            adapter.setTextSize(textSizes[item.itemId])
            true
        }
        popup.show()
    }

    private fun showTranslationMenu(anchor: View) {
        val translations = viewModel.translations
        val popup = PopupMenu(requireContext(), anchor)
        translations.forEachIndexed { i, t ->
            popup.menu.add(0, i, i, "${t.code} · ${t.label}")
        }
        popup.menu.setGroupCheckable(0, true, true)
        popup.menu.getItem(translations.indexOf(viewModel.translation).coerceAtLeast(0)).isChecked = true
        popup.setOnMenuItemClickListener { item ->
            translations.getOrNull(item.itemId)?.let(viewModel::selectTranslation)
            true
        }
        popup.show()
    }

    override fun onDestroyView() {
        binding.chapterList.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
