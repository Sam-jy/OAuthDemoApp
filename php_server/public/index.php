<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);

define('APP_ROOT', dirname(__DIR__));

require APP_ROOT . '/vendor/autoload.php';

require APP_ROOT . '/src/helpers.php';


$request_uri = $_SERVER['REQUEST_URI'];
$request_method = $_SERVER['REQUEST_METHOD'];

$base_path = parse_url($request_uri, PHP_URL_PATH);

if (preg_match('#^/auth/authorize#', $base_path)) {
    require APP_ROOT . '/auth/authorize.php';
} elseif (preg_match('#^/auth/token#', $base_path)) {
    require APP_ROOT . '/auth/token.php';
} elseif (preg_match('#^/api/notes$#', $base_path) && $request_method === 'GET') {
    require APP_ROOT . '/api/notes_list.php';
} elseif (preg_match('#^/api/notes/(\d+)$#', $base_path, $matches) && $request_method === 'GET') {
    $id = $matches[1];
    require APP_ROOT . '/api/note_get.php';
} elseif (preg_match('#^/api/notes$#', $base_path) && $request_method === 'POST') {
    require APP_ROOT . '/api/note_create.php';
} elseif (preg_match('#^/api/notes/(\d+)$#', $base_path, $matches) && $request_method === 'PUT') {
    $id = $matches[1];
    require APP_ROOT . '/api/note_update.php';
} elseif (preg_match('#^/api/notes/(\d+)$#', $base_path, $matches) && $request_method === 'DELETE') {
    $id = $matches[1];
    require APP_ROOT . '/api/note_delete.php';
} elseif ($base_path === '/') {
 
    echo json_encode([
        'api' => 'OAuth 2.0 + OpenID Connect API',
        'version' => '1.0.0',
        'endpoints' => [
            'auth' => [
                'authorize' => '/auth/authorize',
                'token' => '/auth/token'
            ],
            'notes' => [
                'list' => '/api/notes',
                'get' => '/api/notes/{id}',
                'create' => '/api/notes',
                'update' => '/api/notes/{id}',
                'delete' => '/api/notes/{id}'
            ]
        ]
    ]);
} else {

    http_response_code(404);
    echo json_encode([
        'error' => 'not_found',
        'error_description' => 'La ruta solicitada no existe'
    ]);
} 