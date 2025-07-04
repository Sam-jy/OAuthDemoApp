<?php
/**
 * API Endpoint: GET /api/notes/{id}
 * 
 * Obtiene una nota específica del usuario autenticado
 */

// Verificar token y obtener datos del usuario
$user = verifyAccessToken();
$user_id = $user['user_id'];

// Obtener ID de la nota desde la URL
if (!isset($id) || !is_numeric($id)) {
    respondWithError('invalid_request', 'ID de nota inválido', 400);
}

// Obtener nota desde la base de datos
$db = getDbConnection();
$stmt = $db->prepare("
    SELECT id, title, content, created_at, updated_at 
    FROM notes 
    WHERE id = ? AND user_id = ?
");
$stmt->execute([$id, $user_id]);
$note = $stmt->fetch();

if (!$note) {
    respondWithError('not_found', 'Nota no encontrada', 404);
}

// Responder con los datos de la nota
respondWithJson([
    'success' => true,
    'note' => $note
]); 