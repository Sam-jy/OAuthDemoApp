<?php

$user = verifyAccessToken();
$user_id = $user['user_id'];

$json_data = file_get_contents('php://input');
$data = json_decode($json_data, true);

if (!$data || !isset($data['title']) || !isset($data['content'])) {
    respondWithError('invalid_request', 'Faltan campos requeridos (title, content)', 400);
}

$title = trim($data['title']);
$content = trim($data['content']);

if (empty($title)) {
    respondWithError('invalid_request', 'El título no puede estar vacío', 400);
}


$db = getDbConnection();
$stmt = $db->prepare("
    INSERT INTO notes (user_id, title, content) 
    VALUES (?, ?, ?)
");
$stmt->execute([$user_id, $title, $content]);
$note_id = $db->lastInsertId();


$stmt = $db->prepare("
    SELECT id, title, content, created_at, updated_at 
    FROM notes 
    WHERE id = ?
");
$stmt->execute([$note_id]);
$note = $stmt->fetch();


respondWithJson([
    'success' => true,
    'message' => 'Nota creada correctamente',
    'note' => $note
], 201); 