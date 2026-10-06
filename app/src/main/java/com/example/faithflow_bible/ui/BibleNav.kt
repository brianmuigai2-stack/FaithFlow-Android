package com.example.faithflow_bible.ui

import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.faithflow_bible.R

/**
 * Opens the reader, optionally on a specific chapter and verse.
 *
 * The reader lives inside the Bible tab's nested graph. NavController only
 * resolves IDs on the current destination and its parent graphs, so the
 * reader cannot be reached by ID or global action from the other tabs.
 * Deep links are matched recursively from the root graph, so this uses one
 * to skip past the books and chapters lists straight to the text.
 */
fun Fragment.openBible(book: Int? = null, chapter: Int = 1, verse: Int = 0) {
    val uri = Uri.parse("faithflow://reader/${book ?: 0}/$chapter/$verse")
    val request = NavDeepLinkRequest.Builder.fromUri(uri).build()
    val options = NavOptions.Builder()
        // saveState must stay true here. The bottom tabs are driven by NavigationUI, which
        // restores state via popUpTo(startDestination, inclusive=false, saveState=true).
        // Popping to the start without saving would leave it out of the saved-state map, so the
        // next tap on the Home tab would restore this reader entry instead of showing Home.
        .setPopUpTo(R.id.navigation_home, false, true)
        .setLaunchSingleTop(true)
        .build()
    findNavController().navigate(request, options)
}
