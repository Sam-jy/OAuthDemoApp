<?php
/**
 * API Endpoint: PUT /api/notes/{id}
 * 
 * Actualiza una nota existente del usuario autenticado
 */

// Verificar token y obtener datos del usuario
$user = verifyAccessToken();
$user_id = $user['user_id'];

// Obtener ID de la nota desde la URL
if (!isset($id) || !is_numeric($id)) {
    respondWithError('invalid_request', 'ID de nota inválido', 400);
}

// Obtener datos de la solicitud
$json_data = file_get_contents('php://input');
$data = json_decode($json_data, true);

// Verificar datos requeridos
if (!$data || !isset($data['title']) || !isset($data['content'])) {
    respondWithError('invalid_request', 'Faltan campos requeridos (title, content)', 400);
}

$title = trim($data['title']);
$content = trim($data['content']);


if (empty($title)) {
    respondWithError('invalid_request', 'El título no puede estar vacío', 400);
}

$db = getDbConnection();
$stmt = $db->prepare("SELECT id FROM notes WHERE id = ? AND user_id = ?");
$stmt->execute([$id, $user_id]);
if (!$stmt->fetch()) {
    respondWithError('not_found', 'Nota no encontrada o no tienes permiso para editarla', 404);
}

// Actualizar la nota
$stmt = $db->prepare("
    UPDATE notes 
    SET title = ?, content = ? 
    WHERE id = ? AND user_id = ?
");
$stmt->execute([$title, $content, $id, $user_id]);

// Obtener la nota actualizada
$stmt = $db->prepare("
    SELECT id, title, content, created_at, updated_at 
    FROM notes 
    WHERE id = ?
");
$stmt->execute([$id]);
$note = $stmt->fetch();

// Responder con la nota actualizada
respondWithJson([
    'success' => true,
    'message' => 'Nota actualizada correctamente',
    'note' => $note
]); 