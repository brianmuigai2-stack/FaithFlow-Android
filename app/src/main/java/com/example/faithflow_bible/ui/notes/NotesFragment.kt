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
import com.example.faithflow_bible.databinding.FragmentNotesBinding

class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NotesViewModel
    private lateinit var adapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[NotesViewModel::class.java]

        adapter = NotesAdapter { openEditor(it.id.toInt()) }
        binding.notesList.adapter = adapter

        binding.btnAdd.setOnClickListener { openEditor(0) }
        binding.btnEmptyAction.setOnClickListener { openEditor(0) }

        viewModel.notes.observe(viewLifecycleOwner) { notes ->
            adapter.submitList(notes)
            binding.notesList.isVisible = notes.isNotEmpty()
            binding.emptyState.isVisible = notes.isEmpty()
        }
    }

    // Reload when we come back from the editor.
    override fun onStart() {
        super.onStart()
        viewModel.refresh()
    }

    private fun openEditor(noteId: Int) {
        findNavController().navigate(
            R.id.navigation_note_editor,
            Bundle().apply { putInt("note_id", noteId) }
        )
    }

    override fun onDestroyView() {
        binding.notesList.adapter = null
        _binding = null
        super.onDestroyView()
    }
}