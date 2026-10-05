package com.example.faithflow_bible.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.faithflow_bible.R
import com.example.faithflow_bible.data.ReaderPrefs
import com.example.faithflow_bible.data.ThemeMode
import com.example.faithflow_bible.databinding.FragmentSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel
    private var suppressCallbacks = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.themeToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (suppressCallbacks || !isChecked) return@addOnButtonCheckedListener
            viewModel.setTheme(
                when (checkedId) {
                    R.id.themeLight -> ThemeMode.LIGHT
                    R.id.themeDark -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }
            )
        }

        binding.textSizeToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (suppressCallbacks || !isChecked) return@addOnButtonCheckedListener
            val index = when (checkedId) {
                R.id.textSizeSmall -> 0
                R.id.textSizeLarge -> 2
                else -> 1
            }
            viewModel.setTextSize(index)
            renderPreview(index)
        }

        binding.notificationToggle.setOnCheckedChangeListener { _, isChecked ->
            if (suppressCallbacks) return@setOnCheckedChangeListener
            viewModel.setNotificationsEnabled(isChecked)
        }

        binding.btnClearMarks.setOnClickListener { confirmClearMarks() }

        viewModel.ui.observe(viewLifecycleOwner) { ui ->
            suppressCallbacks = true
            binding.themeToggle.check(
                when (ui.theme) {
                    ThemeMode.LIGHT -> R.id.themeLight
                    ThemeMode.DARK -> R.id.themeDark
                    ThemeMode.SYSTEM -> R.id.themeSystem
                }
            )
            binding.textSizeToggle.check(
                when (ui.textSizeIndex) {
                    0 -> R.id.textSizeSmall
                    2 -> R.id.textSizeLarge
                    else -> R.id.textSizeMedium
                }
            )
            binding.notificationToggle.isChecked = ui.notificationsEnabled
            suppressCallbacks = false
            binding.settingsVersion.text = ui.versionName
            renderPreview(ui.textSizeIndex)
        }
    }

    /** Shows the chosen size straight away, so the setting has visible feedback. */
    private fun renderPreview(index: Int) {
        val sizes = floatArrayOf(16f, 19f, 22f)
        binding.previewBody.textSize = sizes.getOrElse(index) { 19f }
    }

    private fun confirmClearMarks() {
        val marked = ReaderPrefs(requireContext()).allMarks().size
        if (marked == 0) {
            Snackbar.make(binding.root, R.string.ff_nothing_to_clear, Snackbar.LENGTH_SHORT).show()
            return
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.ff_clear_marks_q)
            .setMessage(getString(R.string.ff_clear_marks_body, marked))
            .setNegativeButton(R.string.ff_cancel, null)
            .setPositiveButton(R.string.ff_clear) { _, _ ->
                viewModel.clearMarks()
                Snackbar.make(binding.root, R.string.ff_marks_cleared, Snackbar.LENGTH_SHORT).show()
            }
            .show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}