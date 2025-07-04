<?php
/**
 * Funciones auxiliares para la API
 */

/**
 * Obtiene una conexión a la base de datos
 */
function getDbConnection() {
    static $conn = null;
    
    if ($conn === null) {
        $config = require APP_ROOT . '/config/database.php';
        
        try {
            $dsn = "mysql:host={$config['host']};dbname={$config['dbname']};charset={$config['charset']}";
            $options = [
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                PDO::ATTR_EMULATE_PREPARES => false,
            ];
            
            $conn = new PDO($dsn, $config['username'], $config['password'], $options);
        } catch (PDOException $e) {
            respondWithError('database_error', $e->getMessage(), 500);
        }
    }
    
    return $conn;
}

/**
 * Verifica el token de acceso OAuth y devuelve los datos del usuario
 */
function verifyAccessToken() {
    // Obtener el token del encabezado Authorization
    $headers = getallheaders();
    $authHeader = isset($headers['Authorization']) ? $headers['Authorization'] : '';
    
    if (empty($authHeader) || !preg_match('/Bearer\s+(.*)$/i', $authHeader, $matches)) {
        respondWithError('unauthorized', 'Token de acceso no proporcionado', 401);
    }
    
    $accessToken = $matches[1];
    
    // Verificar token en la base de datos
    $db = getDbConnection();
    $stmt = $db->prepare("
        SELECT at.*, u.id as user_id, u.username, u.email, u.full_name
        FROM oauth_access_tokens at
        LEFT JOIN users u ON at.user_id = u.id
        WHERE at.access_token = ? AND at.expires > NOW()
    ");
    $stmt->execute([$accessToken]);
    $tokenData = $stmt->fetch();
    
    if (!$tokenData) {
        respondWithError('unauthorized', 'Token de acceso inválido o expirado', 401);
    }
    
    // Verificar que el scope incluya 'notes'
    if (!str_contains($tokenData['scope'], 'notes')) {
        respondWithError('insufficient_scope', 'El token no tiene el scope requerido', 403);
    }
    
    return [
        'user_id' => $tokenData['user_id'],
        'username' => $tokenData['username'],
        'email' => $tokenData['email'],
        'full_name' => $tokenData['full_name'],
        'scope' => $tokenData['scope']
    ];
}

/**
 * Envía una respuesta JSON al cliente
 */
function respondWithJson($data, $statusCode = 200) {
    http_response_code($statusCode);
    header('Content-Type: application/json');
    echo json_encode($data);
    exit;
}

/**
 * Envía una respuesta de error al cliente
 */
function respondWithError($error, $description, $statusCode = 400) {
    http_response_code($statusCode);
    header('Content-Type: application/json');
    echo json_encode([
        'error' => $error,
        'error_description' => $description
    ]);
    exit;
} 