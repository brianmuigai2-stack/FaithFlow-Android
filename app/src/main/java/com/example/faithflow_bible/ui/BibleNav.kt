package com.example.faithflow_bible.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.faithflow_bible.R

/** Opens the Bible tab, optionally on a specific chapter and verse. */
fun Fragment.openBible(book: Int? = null, chapter: Int = 1, verse: Int = 0) {
    val args = Bundle()
    if (book != null) {
        args.putInt("book", book)
        args.putInt("chapter", chapter)
        args.putInt("verse", verse)
    }
    val options = NavOptions.Builder()
        // saveState must stay true here. The bottom tabs are driven by NavigationUI, which
        // restores state via popUpTo(startDestination, inclusive=false, saveState=true).
        // Popping to Home without saving would leave Home out of the saved-state map, so the
        // next tap on the Home tab would restore this Bible entry instead of showing Home.
        .setPopUpTo(R.id.navigation_home, false, true)
        .setLaunchSingleTop(true)
        .build()
    findNavController().navigate(R.id.navigation_bible, args, options)
}