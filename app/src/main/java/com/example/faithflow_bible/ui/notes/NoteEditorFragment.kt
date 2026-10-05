package com.example.faithflow_bible.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.faithflow_bible.R
import com.example.faithflow_bible.databinding.FragmentNoteEditorBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class NoteEditorFragment : Fragment() {

    private var _binding: FragmentNoteEditorBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NoteEditorViewModel

    private var deleted = false
    private var manualSave = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[NoteEditorViewModel::class.java]

        // After a rotation the text fields restore themselves, so only fill on the first creation.
        val firstCreate = savedInstanceState == null
        val noteId = arguments?.getInt("note_id", 0) ?: 0
        viewModel.start(noteId.toLong())

        if (firstCreate && noteId == 0) prefillFromVerse()
        renderMode()

        viewModel.note.observe(viewLifecycleOwner) { note ->
            if (note != null && firstCreate) {
                binding.inputTitle.setText(note.title)
                binding.inputReference.setText(note.reference)
                binding.inputContent.setText(note.content)
                viewModel.lastSaved = NoteDraft(note.title, note.reference, note.content)
            }
        }
        viewModel.events.observe(viewLifecycleOwner) { event ->
            if (event == null) return@observe
            when (event) {
                EditorEvent.Saved -> {
                    renderMode()
                    if (manualSave) {
                        manualSave = false
                        flashSaved()
                    }
                }
                EditorEvent.Deleted -> findNavController().navigateUp()
            }
            viewModel.eventHandled()
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.btnSave.setOnClickListener { saveNow(manual = true) }
        binding.btnDelete.setOnClickListener { confirmDelete() }
    }

    /** Coming from the verse menu: start with the reference and the verse quoted. */
    private fun prefillFromVerse() {
        val reference = arguments?.getString("reference").orEmpty()
        val quote = arguments?.getString("quote").orEmpty()
        if (reference.isNotEmpty()) binding.inputReference.setText(reference)
        if (quote.isNotEmpty()) {
            binding.inputContent.setText("\u201C$quote\u201D\n\n")
            binding.inputContent.setSelection(binding.inputContent.text?.length ?: 0)
            binding.inputContent.requestFocus()
        }
        // what we just filled in doesn't count as something worth saving
        viewModel.lastSaved = currentFields()
    }

    private fun currentFields() = NoteDraft(
        binding.inputTitle.text?.toString().orEmpty(),
        binding.inputReference.text?.toString().orEmpty(),
        binding.inputContent.text?.toString().orEmpty()
    )

    private fun renderMode() {
        val exists = viewModel.noteId > 0L
        binding.editorTitle.setText(if (exists) R.string.ff_edit_note else R.string.ff_new_note)
        binding.btnDelete.isVisible = exists
    }

    private fun saveNow(manual: Boolean) {
        val fields = currentFields()
        val unchanged = fields == viewModel.lastSaved
        // A brand-new note still needs writing out, even when only the verse quote was prefilled.
        if (unchanged && (!manual || viewModel.noteId > 0L)) {
            if (manual) flashSaved()
            return
        }
        if (manual) manualSave = true
        if (viewModel.save(fields.title, fields.reference, fields.content)) {
            viewModel.lastSaved = fields
        } else if (manual) {
            manualSave = false
            Snackbar.make(binding.root, R.string.ff_write_something, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun flashSaved() {
        if (_binding == null) return
        binding.savedIndicator.animate().cancel()
        binding.savedIndicator.animate().alpha(1f).setDuration(150).withEndAction {
            _binding?.savedIndicator?.animate()?.alpha(0f)?.setStartDelay(1500)?.setDuration(300)?.start()
        }.start()
    }

    private fun confirmDelete() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.ff_delete_note_q)
            .setNegativeButton(R.string.ff_cancel, null)
            .setPositiveButton(R.string.ff_delete) { _, _ ->
                deleted = true
                viewModel.delete()
            }
            .show()
    }

    // Leaving the screen keeps whatever was typed, so nothing gets lost.
    override fun onPause() {
        super.onPause()
        if (!deleted && _binding != null) saveNow(manual = false)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}