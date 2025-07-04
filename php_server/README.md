# Servidor PHP para OAuth 2.0 + OpenID Connect

Este directorio contiene el código del servidor PHP que implementa OAuth 2.0 + OpenID Connect para autenticación y una API REST para operaciones CRUD de notas.

## Estructura del Proyecto

- `config/` - Archivos de configuración
- `auth/` - Implementación de OAuth 2.0 + OpenID Connect
- `api/` - Endpoints de la API para CRUD de notas
- `db/` - Scripts de base de datos

## Requisitos

- PHP 7.4+
- Servidor web (Apache, Nginx)
- MySQL o MariaDB
- Composer (para instalar dependencias)

## Instalación

1. Copia estos archivos a tu servidor web
2. Configura la base de datos ejecutando `db/setup.sql`
3. Configura las credenciales en `config/database.php` y `config/oauth.php`
4. Instala las dependencias con Composer:

```
cd /ruta/al/servidor
composer install
```

5. Configura tu servidor web para apuntar a la carpeta `public/` como directorio raíz

## URLs de la API

- **Autenticación**: `/auth/token` - Obtener token
- **Notas**:
  - `GET /api/notes` - Listar todas las notas
  - `GET /api/notes/{id}` - Obtener una nota específica
  - `POST /api/notes` - Crear una nueva nota
  - `PUT /api/notes/{id}` - Actualizar una nota
  - `DELETE /api/notes/{id}` - Eliminar una nota

Todas las operaciones de la API requieren un token de acceso válido, que debe enviarse en el encabezado `Authorization: Bearer {token}`. 