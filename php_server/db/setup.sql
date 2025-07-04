
CREATE DATABASE IF NOT EXISTS oauth_notes_app
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_general_ci;

USE oauth_notes_app;


CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS oauth_clients (
    client_id VARCHAR(80) NOT NULL,
    client_secret VARCHAR(80) NOT NULL,
    redirect_uri VARCHAR(2000) NOT NULL,
    grant_types VARCHAR(80),
    scope VARCHAR(4000),
    user_id VARCHAR(80),
    PRIMARY KEY (client_id)
);

CREATE TABLE IF NOT EXISTS oauth_access_tokens (
    access_token VARCHAR(40) NOT NULL,
    client_id VARCHAR(80) NOT NULL,
    user_id VARCHAR(80),
    expires TIMESTAMP NOT NULL,
    scope VARCHAR(4000),
    PRIMARY KEY (access_token)
);


CREATE TABLE IF NOT EXISTS oauth_authorization_codes (
    authorization_code VARCHAR(40) NOT NULL,
    client_id VARCHAR(80) NOT NULL,
    user_id VARCHAR(80),
    redirect_uri VARCHAR(2000),
    expires TIMESTAMP NOT NULL,
    scope VARCHAR(4000),
    id_token VARCHAR(1000),
    PRIMARY KEY (authorization_code)
);


CREATE TABLE IF NOT EXISTS oauth_refresh_tokens (
    refresh_token VARCHAR(40) NOT NULL,
    client_id VARCHAR(80) NOT NULL,
    user_id VARCHAR(80),
    expires TIMESTAMP NOT NULL,
    scope VARCHAR(4000),
    PRIMARY KEY (refresh_token)
);


CREATE TABLE IF NOT EXISTS oauth_scopes (
    scope VARCHAR(80) NOT NULL,
    is_default BOOLEAN,
    PRIMARY KEY (scope)
);


CREATE TABLE IF NOT EXISTS oauth_jwt (
    client_id VARCHAR(80) NOT NULL,
    subject VARCHAR(80),
    public_key VARCHAR(2000) NOT NULL,
    PRIMARY KEY (client_id)
);


CREATE TABLE IF NOT EXISTS notes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO oauth_clients (client_id, client_secret, redirect_uri, grant_types, scope, user_id)
VALUES 
('tu_cliente_id', 'tu_cliente_secret', 'com.example.oauthdemoapp://oauth', 'authorization_code refresh_token', 'openid profile email notes', NULL)
ON DUPLICATE KEY UPDATE client_secret = VALUES(client_secret), redirect_uri = VALUES(redirect_uri);


INSERT INTO oauth_scopes (scope, is_default)
VALUES 
('openid', true),
('profile', true),
('email', true),
('notes', true)
ON DUPLICATE KEY UPDATE is_default = VALUES(is_default);

INSERT INTO users (username, password, email, full_name)
VALUES 
('usuario_prueba', '$2y$10$8MIocGf9pz8fVTwPQadl7.5mJ.Zp1LZ47w8X9WCOHZrVWrKGOXsHu', 'prueba@ejemplo.com', 'Usuario de Prueba')
ON DUPLICATE KEY UPDATE password = VALUES(password);
