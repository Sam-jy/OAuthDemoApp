package com.example.oauthdemoapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oauthdemoapp.R;
import com.example.oauthdemoapp.auth.AuthManager;

public class MainActivity extends AppCompatActivity {
    private static final int RC_AUTH = 100;
    private AuthManager authManager;
    private Button loginButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authManager = new AuthManager(this);
        
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);

        loginButton.setOnClickListener(v -> startAuth());

        if (authManager.isAuthorized()) {
            navigateToNoteList();
        }
    }

    private void startAuth() {
        showLoading(true);
        authManager.startAuthorizationFlow(this, RC_AUTH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == RC_AUTH) {
            if (data != null) {
                authManager.handleAuthorizationResponse(data, new AuthManager.AuthCallback() {
                    @Override
                    public void onSuccess() {
                        showLoading(false);
                        navigateToNoteList();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        showLoading(false);
                        Toast.makeText(MainActivity.this, 
                                "Error de autenticación: " + errorMessage, 
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                showLoading(false);
                Toast.makeText(this, "Autenticación cancelada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToNoteList() {
        Intent intent = new Intent(this, NoteListActivity.class);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!isLoading);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        authManager.dispose();
    }
} 