package com.example.oauthdemoapp.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.oauthdemoapp.config.AppConfig;

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
    
    private AuthState authState;
    private AuthorizationService authService;
    private final SharedPreferences preferences;

    public AuthManager(Context context) {
        preferences = context.getSharedPreferences(AppConfig.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        authState = readAuthState();
        authService = new AuthorizationService(context);
    }

    public void startAuthorizationFlow(Activity activity, int requestCode) {
        AuthorizationServiceConfiguration serviceConfig =
                new AuthorizationServiceConfiguration(
                        AppConfig.OAUTH_AUTH_ENDPOINT, 
                        AppConfig.OAUTH_TOKEN_ENDPOINT);

        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                serviceConfig,
                AppConfig.OAUTH_CLIENT_ID,
                ResponseTypeValues.CODE,
                AppConfig.OAUTH_REDIRECT_URI);

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
            
            ClientAuthentication clientAuth = new ClientSecretBasic(AppConfig.OAUTH_CLIENT_SECRET);

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
        preferences.edit().remove(AppConfig.AUTH_STATE_KEY).apply();
    }

    private void writeAuthState() {
        if (authState == null) {
            preferences.edit().remove(AppConfig.AUTH_STATE_KEY).apply();
            return;
        }

        try {
            preferences.edit()
                    .putString(AppConfig.AUTH_STATE_KEY, authState.toJsonString())
                    .apply();
        } catch (Exception e) {
            Log.e(TAG, "Error al guardar estado de autenticación", e);
        }
    }

    @Nullable
    private AuthState readAuthState() {
        String jsonString = preferences.getString(AppConfig.AUTH_STATE_KEY, null);
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