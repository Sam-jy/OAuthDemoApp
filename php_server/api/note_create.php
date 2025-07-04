<?php
/**
 * API Endpoint: POST /api/notes
 * 
 * Crea una nueva nota para el usuario autenticado
 */

// Verificar token y obtener datos del usuario
$user = verifyAccessToken();
$user_id = $user['user_id'];

// Obtener datos de la solicitud
$json_data = file_get_contents('php://input');
$data = json_decode($json_data, true);

// Verificar datos requeridos
if (!$data || !isset($data['title']) || !isset($data['content'])) {
    respondWithError('invalid_request', 'Faltan campos requeridos (title, content)', 400);
}

$title = trim($data['title']);
$content = trim($data['content']);

// Validar datos
if (empty($title)) {
    respondWithError('invalid_request', 'El título no puede estar vacío', 400);
}

// Insertar nota en la base de datos
$db = getDbConnection();
$stmt = $db->prepare("
    INSERT INTO notes (user_id, title, content) 
    VALUES (?, ?, ?)
");
$stmt->execute([$user_id, $title, $content]);
$note_id = $db->lastInsertId();

// Obtener la nota recién creada
$stmt = $db->prepare("
    SELECT id, title, content, created_at, updated_at 
    FROM notes 
    WHERE id = ?
");
$stmt->execute([$note_id]);
$note = $stmt->fetch();

// Responder con la nota creada
respondWithJson([
    'success' => true,
    'message' => 'Nota creada correctamente',
    'note' => $note
], 201); 