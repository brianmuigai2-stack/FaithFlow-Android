package com.example.faithflow_bible.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.concurrent.Executors

data class Note(
    val id: Long,
    val title: String,
    val reference: String,
    val content: String,
    val updatedAt: Long
)

/** Notes live in a small on-device SQLite database. */
class NotesRepository private constructor(context: Context) :
    SQLiteOpenHelper(context.applicationContext, "faithflow_notes.db", null, 1) {

    // One shared worker, so a save always finishes before the list reloads.
    private val io = Executors.newSingleThreadExecutor()

    fun runAsync(block: () -> Unit) {
        io.execute { block() }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE notes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "reference TEXT NOT NULL, " +
                "content TEXT NOT NULL, " +
                "updated_at INTEGER NOT NULL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    fun all(): List<Note> =
        readableDatabase.query("notes", null, null, null, null, null, "updated_at DESC").use { c ->
            val notes = ArrayList<Note>(c.count)
            while (c.moveToNext()) notes += c.toNote()
            notes
        }

    fun get(id: Long): Note? =
        readableDatabase.query("notes", null, "id = ?", arrayOf(id.toString()), null, null, null).use { c ->
            if (c.moveToFirst()) c.toNote() else null
        }

    /** Inserts when [Note.id] is 0, otherwise updates. Returns the note's id. */
    fun save(note: Note): Long {
        val values = ContentValues().apply {
            put("title", note.title)
            put("reference", note.reference)
            put("content", note.content)
            put("updated_at", note.updatedAt)
        }
        return if (note.id > 0) {
            writableDatabase.update("notes", values, "id = ?", arrayOf(note.id.toString()))
            note.id
        } else {
            writableDatabase.insert("notes", null, values)
        }
    }

    fun delete(id: Long) {
        writableDatabase.delete("notes", "id = ?", arrayOf(id.toString()))
    }

    private fun Cursor.toNote() = Note(
        id = getLong(getColumnIndexOrThrow("id")),
        title = getString(getColumnIndexOrThrow("title")),
        reference = getString(getColumnIndexOrThrow("reference")),
        content = getString(getColumnIndexOrThrow("content")),
        updatedAt = getLong(getColumnIndexOrThrow("updated_at"))
    )

    companion object {
        @Volatile
        private var instance: NotesRepository? = null

        fun get(context: Context): NotesRepository =
            instance ?: synchronized(this) {
                instance ?: NotesRepository(context).also { instance = it }
            }
    }
}