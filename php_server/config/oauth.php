<?php
/**
 * Configuración de OAuth 2.0
 */
return [
    'client_id' => 'tu_cliente_id', // Cambiar esto
    'client_secret' => 'tu_cliente_secret', // Cambiar esto
    'redirect_uri' => 'com.example.oauthdemoapp://oauth',
    'scopes' => ['openid', 'profile', 'email', 'notes'],
    'access_token_lifetime' => 3600,
    'refresh_token_lifetime' => 2592000, 
    'auth_code_lifetime' => 600,
    'use_jwt_access_tokens' => true,
    'jwt_secret_key' => 'tu_clave_secreta_jwt', // Cambiar esto - Debe ser una cadena segura aleatoria
]; 