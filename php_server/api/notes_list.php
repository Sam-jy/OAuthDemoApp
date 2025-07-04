<?php
/**
 * API Endpoint: GET /api/notes
 * 
 * Lista todas las notas del usuario autenticado
 */

// Verificar token y obtener datos del usuario
$user = verifyAccessToken();
$user_id = $user['user_id'];

// Obtener notas del usuario desde la base de datos
$db = getDbConnection();
$stmt = $db->prepare("
    SELECT id, title, content, created_at, updated_at 
    FROM notes 
    WHERE user_id = ? 
    ORDER BY updated_at DESC
");
$stmt->execute([$user_id]);
$notes = $stmt->fetchAll();

// Responder con la lista de notas
respondWithJson([
    'success' => true,
    'count' => count($notes),
    'notes' => $notes
]); 