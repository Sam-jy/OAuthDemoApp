<?php
/**
 * Endpoint de autorización OAuth 2.0
 * 
 * Maneja solicitudes de autorización y redirige al usuario
 * después de la autenticación exitosa
 */

// Verificar que se proporcionen los parámetros requeridos
$required_params = ['response_type', 'client_id', 'redirect_uri'];
foreach ($required_params as $param) {
    if (!isset($_GET[$param])) {
        respondWithError('invalid_request', "Falta el parámetro requerido: {$param}");
    }
}

$response_type = $_GET['response_type'];
$client_id = $_GET['client_id'];
$redirect_uri = $_GET['redirect_uri'];
$scope = isset($_GET['scope']) ? $_GET['scope'] : '';
$state = isset($_GET['state']) ? $_GET['state'] : '';

// Verificar que response_type sea válido (solo se admite 'code')
if ($response_type !== 'code') {
    respondWithError('unsupported_response_type', 'Solo se admite response_type=code');
}

// Verificar que el cliente exista
$db = getDbConnection();
$stmt = $db->prepare("SELECT * FROM oauth_clients WHERE client_id = ?");
$stmt->execute([$client_id]);
$client = $stmt->fetch();

if (!$client) {
    respondWithError('invalid_client', 'Cliente no reconocido');
}

// Verificar que la URI de redirección coincida
if ($client['redirect_uri'] !== $redirect_uri) {
    respondWithError('invalid_request', 'La URI de redirección no coincide con la registrada para el cliente');
}

// Manejo de la sesión y autenticación del usuario
session_start();

// Si el usuario ya está autenticado, generar código de autorización
if (isset($_SESSION['user_id'])) {
    createAuthorizationCode($client_id, $_SESSION['user_id'], $redirect_uri, $scope, $state);
}

// Si se recibió un formulario de inicio de sesión, verificar credenciales
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['username']) && isset($_POST['password'])) {
    $username = $_POST['username'];
    $password = $_POST['password'];
    
    // Verificar credenciales en la base de datos
    $stmt = $db->prepare("SELECT * FROM users WHERE username = ?");
    $stmt->execute([$username]);
    $user = $stmt->fetch();
    
    if ($user && password_verify($password, $user['password'])) {
        // Autenticación exitosa, guardar usuario en sesión
        $_SESSION['user_id'] = $user['id'];
        $_SESSION['username'] = $user['username'];
        
        // Generar código de autorización
        createAuthorizationCode($client_id, $user['id'], $redirect_uri, $scope, $state);
    } else {
        $error_message = 'Credenciales inválidas';
        // Mostrar formulario de inicio de sesión con mensaje de error
    }
}

// Mostrar formulario de inicio de sesión
?>
<!DOCTYPE html>
<html>
<head>
    <title>Iniciar sesión - OAuth 2.0 Demo</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 500px;
            margin: 0 auto;
            padding: 20px;
        }
        .container {
            border: 1px solid #ddd;
            padding: 20px;
            border-radius: 5px;
        }
        h1 {
            text-align: center;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
        }
        input[type="text"], input[type="password"] {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        button {
            background-color: #4CAF50;
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            width: 100%;
        }
        .error {
            color: red;
            margin-bottom: 15px;
        }
        .client-info {
            background-color: #f9f9f9;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .scope-list {
            list-style: none;
            padding-left: 0;
        }
        .scope-item {
            margin-bottom: 5px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Iniciar sesión</h1>
        
        <div class="client-info">
            <p><strong>Aplicación:</strong> <?php echo htmlspecialchars($client_id); ?></p>
            <p><strong>Permisos solicitados:</strong></p>
            <ul class="scope-list">
                <?php foreach (explode(' ', $scope) as $scopeItem): ?>
                <li class="scope-item"><?php echo htmlspecialchars($scopeItem); ?></li>
                <?php endforeach; ?>
            </ul>
        </div>
        
        <?php if (isset($error_message)): ?>
        <div class="error"><?php echo htmlspecialchars($error_message); ?></div>
        <?php endif; ?>
        
        <form method="post">
            <div class="form-group">
                <label for="username">Usuario:</label>
                <input type="text" id="username" name="username" required>
            </div>
            
            <div class="form-group">
                <label for="password">Contraseña:</label>
                <input type="password" id="password" name="password" required>
            </div>
            
            <button type="submit">Iniciar sesión</button>
        </form>
    </div>
</body>
</html>

<?php
/**
 * Genera un código de autorización y redirige al cliente
 */
function createAuthorizationCode($client_id, $user_id, $redirect_uri, $scope, $state) {
    global $db;
    
    // Generar código de autorización
    $code = bin2hex(random_bytes(20));
    
    // Establecer expiración (10 minutos)
    $expires = date('Y-m-d H:i:s', time() + 600);
    
    // Guardar código en la base de datos
    $stmt = $db->prepare("
        INSERT INTO oauth_authorization_codes 
        (authorization_code, client_id, user_id, redirect_uri, expires, scope)
        VALUES (?, ?, ?, ?, ?, ?)
    ");
    $stmt->execute([$code, $client_id, $user_id, $redirect_uri, $expires, $scope]);
    
    // Construir URL de redirección
    $redirect_url = $redirect_uri;
    $redirect_url .= (strpos($redirect_uri, '?') !== false) ? '&' : '?';
    $redirect_url .= 'code=' . urlencode($code);
    
    if (!empty($state)) {
        $redirect_url .= '&state=' . urlencode($state);
    }
    
    // Redirigir al cliente
    header('Location: ' . $redirect_url);
    exit;
} 