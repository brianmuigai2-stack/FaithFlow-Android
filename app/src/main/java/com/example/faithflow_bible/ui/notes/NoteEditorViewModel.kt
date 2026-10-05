package com.example.faithflow_bible.ui.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.faithflow_bible.R
import com.example.faithflow_bible.data.Note
import com.example.faithflow_bible.data.NotesRepository

sealed interface EditorEvent {
    data object Saved : EditorEvent
    data object Deleted : EditorEvent
}

/** The three editor fields, kept so a rotation can tell whether anything actually changed. */
data class NoteDraft(val title: String, val reference: String, val content: String) {
    companion object {
        val EMPTY = NoteDraft("", "", "")
    }
}

class NoteEditorViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = NotesRepository.get(app)

    private val _note = MutableLiveData<Note?>()
    val note: LiveData<Note?> = _note

    private val _events = MutableLiveData<EditorEvent?>()
    val events: LiveData<EditorEvent?> = _events

    /** 0 until the note has been saved for the first time. */
    @Volatile
    var noteId: Long = 0L
        private set

    private var started = false

    /** Survives rotation so we don't rewrite an unchanged note. */
    @Volatile
    var lastSaved: NoteDraft? = null

    fun start(id: Long) {
        if (started) return
        started = true
        noteId = id
        if (id > 0) repo.runAsync { _note.postValue(repo.get(id)) }
    }

    /** Call once the fragment has acted on an event, so a rotation doesn't replay it. */
    fun eventHandled() {
        _events.value = null
    }

    /** Returns false (and saves nothing) when every field is empty. */
    fun save(title: String, reference: String, content: String): Boolean {
        val t = title.trim()
        val r = reference.trim()
        val c = content.trim()
        if (t.isEmpty() && r.isEmpty() && c.isEmpty()) return false
        val untitled = getApplication<Application>().getString(R.string.ff_untitled)
        repo.runAsync {
            noteId = repo.save(Note(noteId, t.ifEmpty { untitled }, r, c, System.currentTimeMillis()))
            _events.postValue(EditorEvent.Saved)
        }
        return true
    }

    fun delete() {
        repo.runAsync {
            if (noteId > 0) repo.delete(noteId)
            _events.postValue(EditorEvent.Deleted)
        }
    }
}