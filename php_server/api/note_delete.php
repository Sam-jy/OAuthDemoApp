<?php
/**
 * API Endpoint: DELETE /api/notes/{id}
 * 
 * Elimina una nota existente del usuario autenticado
 */

// Verificar token y obtener datos del usuario
$user = verifyAccessToken();
$user_id = $user['user_id'];

// Obtener ID de la nota desde la URL
if (!isset($id) || !is_numeric($id)) {
    respondWithError('invalid_request', 'ID de nota inválido', 400);
}

// Verificar que la nota exista y pertenezca al usuario
$db = getDbConnection();
$stmt = $db->prepare("SELECT id FROM notes WHERE id = ? AND user_id = ?");
$stmt->execute([$id, $user_id]);
if (!$stmt->fetch()) {
    respondWithError('not_found', 'Nota no encontrada o no tienes permiso para eliminarla', 404);
}

// Eliminar la nota
$stmt = $db->prepare("DELETE FROM notes WHERE id = ? AND user_id = ?");
$stmt->execute([$id, $user_id]);

// Responder con éxito
respondWithJson([
    'success' => true,
    'message' => 'Nota eliminada correctamente'
]); 