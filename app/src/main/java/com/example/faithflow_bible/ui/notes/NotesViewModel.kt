package com.example.faithflow_bible.ui.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.faithflow_bible.data.Note
import com.example.faithflow_bible.data.NotesRepository

class NotesViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = NotesRepository.get(app)

    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    fun refresh() {
        repo.runAsync { _notes.postValue(repo.all()) }
    }
}