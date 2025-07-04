package com.example.oauthdemoapp.config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.oauthdemoapp.model.Note;

import java.util.ArrayList;
import java.util.List;

public class SQLiteConexion extends SQLiteOpenHelper {
    private static final String DB_NAME = "NotesDB";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NOTES = "notes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public SQLiteConexion(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_NOTES + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TITLE + " TEXT NOT NULL, " +
                        COLUMN_CONTENT + " TEXT NOT NULL, " +
                        COLUMN_TIMESTAMP + " INTEGER NOT NULL" +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    public long insertNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, note.getTitle());
        cv.put(COLUMN_CONTENT, note.getContent());
        cv.put(COLUMN_TIMESTAMP, note.getTimestamp());
        long id = db.insert(TABLE_NOTES, null, cv);
        db.close();
        return id;
    }

    public List<Note> getAllNotes() {
        List<Note> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES + " ORDER BY " + COLUMN_TIMESTAMP + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                if (idIndex >= 0) note.setId(cursor.getInt(idIndex));
                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                if (titleIndex >= 0) note.setTitle(cursor.getString(titleIndex));
                int contentIndex = cursor.getColumnIndex(COLUMN_CONTENT);
                if (contentIndex >= 0) note.setContent(cursor.getString(contentIndex));
                int timestampIndex = cursor.getColumnIndex(COLUMN_TIMESTAMP);
                if (timestampIndex >= 0) note.setTimestamp(cursor.getLong(timestampIndex));
                list.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public Note getNoteById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, null, 
                COLUMN_ID + " = ?", 
                new String[] { String.valueOf(id) }, 
                null, null, null);
        Note note = null;
        if (cursor.moveToFirst()) {
            note = new Note();
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            if (idIndex >= 0) note.setId(cursor.getInt(idIndex));
            int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
            if (titleIndex >= 0) note.setTitle(cursor.getString(titleIndex));
            int contentIndex = cursor.getColumnIndex(COLUMN_CONTENT);
            if (contentIndex >= 0) note.setContent(cursor.getString(contentIndex));
            int timestampIndex = cursor.getColumnIndex(COLUMN_TIMESTAMP);
            if (timestampIndex >= 0) note.setTimestamp(cursor.getLong(timestampIndex));
        }
        cursor.close();
        db.close();
        return note;
    }

    public int deleteNote(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_NOTES, COLUMN_ID + " = ?", 
                new String[] { String.valueOf(id) });
        db.close();
        return rows;
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, note.getTitle());
        cv.put(COLUMN_CONTENT, note.getContent());
        cv.put(COLUMN_TIMESTAMP, note.getTimestamp());
        int rows = db.update(TABLE_NOTES, cv, 
                COLUMN_ID + " = ?", 
                new String[] { String.valueOf(note.getId()) });
        db.close();
        return rows;
    }
} 