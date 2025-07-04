package com.example.oauthdemoapp.config;

import android.content.Context;
import android.net.Uri;

public class AppConfig {
    // Configuración OAuth
    public static final String OAUTH_CLIENT_ID = "tu_cliente_id";
    public static final String OAUTH_CLIENT_SECRET = "tu_cliente_secret";
    public static final Uri OAUTH_REDIRECT_URI = Uri.parse("com.example.oauthdemoapp://oauth");
    public static final Uri OAUTH_AUTH_ENDPOINT = Uri.parse("https://tu-servidor-oauth/auth");
    public static final Uri OAUTH_TOKEN_ENDPOINT = Uri.parse("https://tu-servidor-oauth/token");
    
    // Configuración API
    public static final String API_BASE_URL = "https://tu-servidor-php.com/api/";
    
    // Preferencias compartidas
    public static final String SHARED_PREFS_NAME = "auth_prefs";
    public static final String AUTH_STATE_KEY = "auth_state";
} 