<?php
/**
 * Configuración de OAuth 2.0
 */
return [
    'client_id' => 'tu_cliente_id', // Cambiar esto
    'client_secret' => 'tu_cliente_secret', // Cambiar esto
    'redirect_uri' => 'com.example.oauthdemoapp://oauth',
    'scopes' => ['openid', 'profile', 'email', 'notes'],
    'access_token_lifetime' => 3600, // 1 hora en segundos
    'refresh_token_lifetime' => 2592000, // 30 días en segundos
    'auth_code_lifetime' => 600, // 10 minutos en segundos
    'use_jwt_access_tokens' => true, // Usar JWT para tokens de acceso
    'jwt_secret_key' => 'tu_clave_secreta_jwt', // Cambiar esto - Debe ser una cadena segura aleatoria
]; 