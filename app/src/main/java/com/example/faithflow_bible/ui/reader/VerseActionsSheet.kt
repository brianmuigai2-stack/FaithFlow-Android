package com.example.faithflow_bible.ui.reader

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.faithflow_bible.R
import com.example.faithflow_bible.data.HighlightColors
import com.example.faithflow_bible.data.ReaderPrefs
import com.example.faithflow_bible.databinding.SheetVerseActionsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.card.MaterialCardView

/** The "Verse Actions" menu: copy, note, share, save and highlight. */
class VerseActionsSheet : BottomSheetDialogFragment() {

    private var _binding: SheetVerseActionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefs: ReaderPrefs
    private lateinit var key: String
    private lateinit var reference: String
    private lateinit var verseText: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = SheetVerseActionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = requireArguments()
        prefs = ReaderPrefs(requireContext())

        val chapter = args.getInt(ARG_CHAPTER)
        val verse = args.getInt(ARG_VERSE)
        key = ReaderPrefs.verseKey(args.getInt(ARG_BOOK), chapter, verse)
        reference = "${args.getString(ARG_BOOK_NAME).orEmpty()} $chapter:$verse (WEB)"
        verseText = args.getString(ARG_TEXT).orEmpty().replace("\n", " ")

        binding.sheetReference.text = reference
        binding.sheetVerseText.text = "\u201C$verseText\u201D"

        binding.btnClose.setOnClickListener { dismiss() }
        binding.actionCopy.setOnClickListener { copy() }
        binding.actionShare.setOnClickListener { share() }
        binding.actionNote.setOnClickListener {
            Toast.makeText(requireContext(), R.string.ff_notes_soon, Toast.LENGTH_SHORT).show()
        }
        binding.actionSave.setOnClickListener {
            prefs.toggleSaved(key)
            renderSaved()
            notifyChanged()
        }
        renderSaved()
        renderSwatches()
    }

    private fun renderSaved() {
        val saved = prefs.isSaved(key)
        val color = ContextCompat.getColor(
            requireContext(), if (saved) R.color.ff_accent_teal else R.color.ff_text_primary
        )
        binding.iconSave.imageTintList = ColorStateList.valueOf(color)
        binding.labelSave.setTextColor(color)
    }

    private fun renderSwatches() {
        val ctx = requireContext()
        val density = resources.displayMetrics.density
        val size = (36 * density).toInt()
        val current = prefs.highlightOf(key)

        binding.swatchRow.removeAllViews()
        HighlightColors.swatches.forEachIndexed { index, color ->
            val swatch = MaterialCardView(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginEnd = (12 * density).toInt()
                }
                radius = size / 2f
                cardElevation = 0f
                setCardBackgroundColor(color)
                strokeColor = ContextCompat.getColor(ctx, R.color.ff_text_primary)
                strokeWidth = if (index == current) (3 * density).toInt() else 0
                isClickable = true
                setOnClickListener {
                    // tapping the active colour again removes the highlight
                    prefs.setHighlight(key, if (index == current) -1 else index)
                    notifyChanged()
                    dismiss()
                }
            }
            binding.swatchRow.addView(swatch)
        }
    }

    private fun copy() {
        val clipboard = requireContext().getSystemService(ClipboardManager::class.java)
        clipboard.setPrimaryClip(
            ClipData.newPlainText(reference, "\u201C$verseText\u201D \u2014 $reference")
        )
        Toast.makeText(requireContext(), R.string.ff_copied, Toast.LENGTH_SHORT).show()
        dismiss()
    }

    private fun share() {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "\u201C$verseText\u201D\n\u2014 $reference\n\nShared from FaithFlow"
            )
        }
        startActivity(Intent.createChooser(send, null))
        dismiss()
    }

    private fun notifyChanged() {
        parentFragmentManager.setFragmentResult(RESULT_KEY, Bundle())
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val RESULT_KEY = "verse_actions"
        private const val ARG_BOOK = "book"
        private const val ARG_BOOK_NAME = "book_name"
        private const val ARG_CHAPTER = "chapter"
        private const val ARG_VERSE = "verse"
        private const val ARG_TEXT = "text"

        fun newInstance(
            book: Int, bookName: String, chapter: Int, verse: Int, text: String
        ) = VerseActionsSheet().apply {
            arguments = Bundle().apply {
                putInt(ARG_BOOK, book)
                putString(ARG_BOOK_NAME, bookName)
                putInt(ARG_CHAPTER, chapter)
                putInt(ARG_VERSE, verse)
                putString(ARG_TEXT, text)
            }
        }
    }
}
