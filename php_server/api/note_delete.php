<?php

$user = verifyAccessToken();
$user_id = $user['user_id'];


if (!isset($id) || !is_numeric($id)) {
    respondWithError('invalid_request', 'ID de nota inválido', 400);
}


$db = getDbConnection();
$stmt = $db->prepare("SELECT id FROM notes WHERE id = ? AND user_id = ?");
$stmt->execute([$id, $user_id]);
if (!$stmt->fetch()) {
    respondWithError('not_found', 'Nota no encontrada o no tienes permiso para eliminarla', 404);
}


$stmt = $db->prepare("DELETE FROM notes WHERE id = ? AND user_id = ?");
$stmt->execute([$id, $user_id]);


respondWithJson([
    'success' => true,
    'message' => 'Nota eliminada correctamente'
]); 