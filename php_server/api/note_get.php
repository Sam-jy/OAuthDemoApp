<?php

$user = verifyAccessToken();
$user_id = $user['user_id'];


if (!isset($id) || !is_numeric($id)) {
    respondWithError('invalid_request', 'ID de nota inválido', 400);
}


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


respondWithJson([
    'success' => true,
    'note' => $note
]); 