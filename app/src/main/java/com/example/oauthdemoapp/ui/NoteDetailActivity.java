package com.example.oauthdemoapp.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.oauthdemoapp.R;
import com.example.oauthdemoapp.auth.AuthManager;
import com.example.oauthdemoapp.config.SQLiteConexion;
import com.example.oauthdemoapp.model.Note;

public class NoteDetailActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.example.oauthdemoapp.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.oauthdemoapp.EXTRA_TITLE";
    public static final String EXTRA_CONTENT = "com.example.oauthdemoapp.EXTRA_CONTENT";

    private EditText titleEditText;
    private EditText contentEditText;
    private Button saveButton;
    private Button deleteButton;
    private ProgressBar progressBar;

    private AuthManager authManager;
    private SQLiteConexion conexion;
    
    private int noteId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        progressBar = findViewById(R.id.progressBar);
        
        authManager = new AuthManager(this);
        conexion = new SQLiteConexion(this);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            noteId = intent.getIntExtra(EXTRA_ID, -1);
            isEditMode = true;
            
            titleEditText.setText(intent.getStringExtra(EXTRA_TITLE));
            contentEditText.setText(intent.getStringExtra(EXTRA_CONTENT));
            
            setTitle(R.string.edit_note);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            setTitle(R.string.add_note);
            deleteButton.setVisibility(View.GONE);
        }

        saveButton.setOnClickListener(v -> saveNote());
        deleteButton.setOnClickListener(v -> deleteNote());
    }

    private void saveNote() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            titleEditText.setError("El título no puede estar vacío");
            return;
        }

        showLoading(true);
        
        // Crear o actualizar la nota en SQLite
        new Thread(() -> {
            Note note = new Note(title, content);
            
            boolean success;
            if (isEditMode) {
                note.setId(noteId);
                success = conexion.updateNote(note) > 0;
            } else {
                long id = conexion.insertNote(note);
                success = id > 0;
            }
            
            runOnUiThread(() -> {
                showLoading(false);
                if (success) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, 
                            "Error al guardar la nota", 
                            Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void deleteNote() {
        if (!isEditMode || noteId == -1) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Eliminar Nota")
                .setMessage("¿Estás seguro de que deseas eliminar esta nota?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    showLoading(true);
                    
                    new Thread(() -> {
                        boolean success = conexion.deleteNote(noteId) > 0;
                        
                        runOnUiThread(() -> {
                            showLoading(false);
                            if (success) {
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(this, 
                                        "Error al eliminar la nota", 
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).start();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        saveButton.setEnabled(!isLoading);
        deleteButton.setEnabled(!isLoading);
        titleEditText.setEnabled(!isLoading);
        contentEditText.setEnabled(!isLoading);
    }
} 