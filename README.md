# Bloc de Notas OAuth

Esta es una aplicación Android simple que implementa OAuth 2.0 + OpenID Connect para autenticación, permitiendo a los usuarios gestionar notas personales utilizando SQLite para almacenamiento local.

## Características

- Autenticación mediante OAuth 2.0 + OpenID Connect
- Almacenamiento local con SQLite
- Visualización de lista de notas
- Creación, edición y eliminación de notas
- Interfaz de usuario simple e intuitiva

## Estructura del Proyecto

- **config**: Contiene clases de configuración y acceso a datos
  - `AppConfig.java`: Constantes de configuración centralizadas
  - `SQLiteConexion.java`: Clase para manejar la base de datos SQLite
  
- **model**: Clases de modelo de datos
  - `Note.java`: Representa una nota

- **auth**: Gestión de autenticación
  - `AuthManager.java`: Implementación de OAuth 2.0 y OpenID Connect

- **ui**: Interfaces de usuario
  - `MainActivity.java`: Pantalla de inicio y autenticación
  - `NoteListActivity.java`: Lista de notas
  - `NoteDetailActivity.java`: Creación y edición de notas
  - `NoteAdapter.java`: Adaptador para la lista de notas

## Requisitos

- Android Studio (versión reciente)
- Dispositivo o emulador Android con API 24+

## Configuración

1. **Clonar el repositorio:**

```
git clone https://github.com/tu-usuario/OAuthDemoApp.git
```

2. **Configurar OAuth:**

   Modifica la clase `AppConfig.java` con tus credenciales OAuth:
   
   ```java
   public static final String OAUTH_CLIENT_ID = "tu_cliente_id";
   public static final String OAUTH_CLIENT_SECRET = "tu_cliente_secret";
   public static final Uri OAUTH_AUTH_ENDPOINT = Uri.parse("https://tu-servidor-oauth/auth");
   public static final Uri OAUTH_TOKEN_ENDPOINT = Uri.parse("https://tu-servidor-oauth/token");
   ```

## Flujo de la Aplicación

1. **Autenticación**: El usuario inicia sesión utilizando OAuth 2.0 / OpenID Connect
2. **Lista de Notas**: Una vez autenticado, el usuario ve su lista de notas
3. **Gestión de Notas**: El usuario puede crear, editar o eliminar notas

## Implementación de OAuth 2.0 + OpenID Connect

La aplicación implementa el flujo de código de autorización (Authorization Code Flow) de OAuth 2.0, que es el más seguro para aplicaciones móviles. También utiliza OpenID Connect para obtener información del usuario.
