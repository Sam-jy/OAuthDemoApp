<?php

$user = verifyAccessToken();
$user_id = $user['user_id'];


$db = getDbConnection();
$stmt = $db->prepare("
    SELECT id, title, content, created_at, updated_at 
    FROM notes 
    WHERE user_id = ? 
    ORDER BY updated_at DESC
");
$stmt->execute([$user_id]);
$notes = $stmt->fetchAll();


respondWithJson([
    'success' => true,
    'count' => count($notes),
    'notes' => $notes
]); 