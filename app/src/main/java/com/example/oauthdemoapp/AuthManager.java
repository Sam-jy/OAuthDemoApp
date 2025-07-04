package com.example.oauthdemoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;

public class AuthManager {
    private static final String TAG = "AuthManager";
    private static final String SHARED_PREFERENCES_NAME = "auth_prefs";
    private static final String AUTH_STATE = "auth_state";
    private static final String OAUTH_CLIENT_ID = "tu_cliente_id"; 
    private static final String OAUTH_CLIENT_SECRET = "tu_cliente_secret"; 
    private static final Uri OAUTH_REDIRECT_URI = Uri.parse("com.example.oauthdemoapp://oauth");
    
    private static final Uri OAUTH_AUTH_ENDPOINT = Uri.parse("https://tu-servidor-oauth/auth");
    private static final Uri OAUTH_TOKEN_ENDPOINT = Uri.parse("https://tu-servidor-oauth/token");

    private AuthState authState;
    private AuthorizationService authService;
    private final SharedPreferences preferences;

    public AuthManager(Context context) {
        preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        authState = readAuthState();
        authService = new AuthorizationService(context);
    }

    public void startAuthorizationFlow(Activity activity, int requestCode) {
        AuthorizationServiceConfiguration serviceConfig =
                new AuthorizationServiceConfiguration(OAUTH_AUTH_ENDPOINT, OAUTH_TOKEN_ENDPOINT);

        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                serviceConfig,
                OAUTH_CLIENT_ID,
                ResponseTypeValues.CODE,
                OAUTH_REDIRECT_URI);

        builder.setScopes("openid", "profile", "email");

        AuthorizationRequest request = builder.build();

        Intent authIntent = authService.getAuthorizationRequestIntent(request);
        activity.startActivityForResult(authIntent, requestCode);
    }

    public void handleAuthorizationResponse(Intent data, AuthCallback callback) {
        AuthorizationResponse response = AuthorizationResponse.fromIntent(data);
        AuthorizationException exception = AuthorizationException.fromIntent(data);

        if (response != null) {
            authState = new AuthState(response, exception);
            
            ClientAuthentication clientAuth = new ClientSecretBasic(OAUTH_CLIENT_SECRET);

            authService.performTokenRequest(
                    response.createTokenExchangeRequest(),
                    clientAuth,
                    (tokenResponse, authException) -> {
                        if (tokenResponse != null) {
                            authState.update(tokenResponse, authException);
                            writeAuthState();
                            if (callback != null) {
                                callback.onSuccess();
                            }
                        } else {
                            if (callback != null) {
                                callback.onError(authException.getMessage());
                            }
                        }
                    });
        } else if (exception != null) {
            if (callback != null) {
                callback.onError(exception.getMessage());
            }
        }
    }

    public boolean isAuthorized() {
        return authState != null && authState.isAuthorized();
    }

    public void performActionWithFreshTokens(AuthActionCallback callback) {
        if (authState == null) {
            callback.onError("No está autenticado");
            return;
        }

        authState.performActionWithFreshTokens(authService, (accessToken, idToken, ex) -> {
            if (ex != null) {
                callback.onError("Error al obtener token: " + ex.getMessage());
                return;
            }
            
            if (accessToken == null) {
                callback.onError("Token de acceso es null");
                return;
            }
            
            callback.onTokenAvailable(accessToken);
        });
    }

    public void signOut() {
        authState = null;
        preferences.edit().remove(AUTH_STATE).apply();
    }

    private void writeAuthState() {
        if (authState == null) {
            preferences.edit().remove(AUTH_STATE).apply();
            return;
        }

        try {
            preferences.edit()
                    .putString(AUTH_STATE, authState.toJsonString())
                    .apply();
        } catch (Exception e) {
            Log.e(TAG, "Error al guardar estado de autenticación", e);
        }
    }

    @Nullable
    private AuthState readAuthState() {
        String jsonString = preferences.getString(AUTH_STATE, null);
        if (jsonString != null) {
            try {
                return AuthState.fromJson(jsonString);
            } catch (JSONException e) {
                Log.e(TAG, "Error al leer estado de autenticación", e);
            }
        }
        return null;
    }

    public interface AuthCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    public interface AuthActionCallback {
        void onTokenAvailable(String accessToken);
        void onError(String errorMessage);
    }

    public void dispose() {
        authService.dispose();
    }
} 