package com.example.oauthdemoapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oauthdemoapp.R;
import com.example.oauthdemoapp.auth.AuthManager;
import com.example.oauthdemoapp.config.SQLiteConexion;
import com.example.oauthdemoapp.model.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NoteListActivity extends AppCompatActivity {
    private static final int ADD_NOTE_REQUEST = 1;
    private static final int EDIT_NOTE_REQUEST = 2;
    
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private AuthManager authManager;
    private SQLiteConexion conexion;
    private ProgressBar progressBar;
    private FloatingActionButton addNoteButton;
    
    private List<Note> noteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        
        setTitle(R.string.my_notes);
        
        authManager = new AuthManager(this);
        conexion = new SQLiteConexion(this);
        
        recyclerView = findViewById(R.id.notesRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        addNoteButton = findViewById(R.id.addNoteButton);
        
        setupRecyclerView();
        
        addNoteButton.setOnClickListener(v -> {
            Intent intent = new Intent(NoteListActivity.this, NoteDetailActivity.class);
            startActivityForResult(intent, ADD_NOTE_REQUEST);
        });
        
        loadNotes();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);
        
        adapter.setOnItemClickListener(note -> {
            Intent intent = new Intent(NoteListActivity.this, NoteDetailActivity.class);
            intent.putExtra(NoteDetailActivity.EXTRA_ID, note.getId());
            intent.putExtra(NoteDetailActivity.EXTRA_TITLE, note.getTitle());
            intent.putExtra(NoteDetailActivity.EXTRA_CONTENT, note.getContent());
            startActivityForResult(intent, EDIT_NOTE_REQUEST);
        });
    }

    private void loadNotes() {
        showLoading(true);
        
        // Verificar autenticación antes de cargar las notas
        if (authManager.isAuthorized()) {
            // Obtener notas de la base de datos SQLite
            new Thread(() -> {
                final List<Note> notes = conexion.getAllNotes();
                
                runOnUiThread(() -> {
                    showLoading(false);
                    adapter.setNotes(notes);
                });
            }).start();
        } else {
            showLoading(false);
            logout();
        }
    }

    private void logout() {
        authManager.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            loadNotes(); // Recargar notas después de cualquier operación exitosa
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
} 