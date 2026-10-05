package com.example.faithflow_bible.ui.placeholder

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.faithflow_bible.R

/** One stand-in for the Bible / Saved / Notes tabs. Delete it once those screens exist. */
class ComingSoonFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = TextView(requireContext()).apply {
        gravity = Gravity.CENTER
        textSize = 18f
        setTextColor(ContextCompat.getColor(context, R.color.ff_text_secondary))
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = findNavController().currentDestination?.label?.toString().orEmpty()
        (view as TextView).text = getString(R.string.ff_coming_soon, title)
    }
}
