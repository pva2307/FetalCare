# FetalCare: Aplicación Android con Conexión Bluetooth a Arduino

¡Bienvenido al repositorio de la aplicación Android que desarrollé con Android Studio utilizando Kotlin! Esta aplicación permite la conexión Bluetooth a un dispositivo Arduino para la transmisión de datos. Además, incluye características como inicio de sesión, registro de usuarios, integración con Firebase para autenticación y base de datos en tiempo real. 
## Contenido del Repositorio

### 1. Estructura del Proyecto

- **app**: Contiene el código fuente de la aplicación Android.
- **docs**: Documentación adicional.
- **arduino_code**: Código fuente para el programa en el dispositivo Arduino.

### 2. Configuración

Antes de ejecutar la aplicación, asegúrate de realizar los siguientes pasos:

- Configura la conexión Bluetooth en el archivo de configuración de Bluetooth (`BluetoothConfig.kt`).
- Configura la clave de API de Firebase en el archivo de configuración de Firebase (`google-services.json`).

### 3. Características

- **Conexión Bluetooth**: Establece una conexión Bluetooth con un dispositivo Arduino para la transmisión de datos.
- **Inicio de Sesión y Registro**: Permite a los usuarios iniciar sesión o registrarse en la aplicación.
- **Firebase Authentication**: Utiliza Firebase para autenticar a los usuarios de la aplicación.
- **Firebase Realtime Database**: Almacena y recupera datos en tiempo real utilizando Firebase Realtime Database.
- **Cambio de Correo y Contraseña**: Permite a los usuarios cambiar su dirección de correo electrónico y contraseña.
- **Historial de Cambios**: Mantiene un registro de los cambios de correo y contraseña en una base de datos local.

### 4. Instrucciones de Uso

Para probar la aplicación:

1. Clona este repositorio: `git clone https://github.com/pva01/FetalCare.git`
2. Abre el proyecto en Android Studio.
3. Configura la conexión Bluetooth y las claves de Firebase según las instrucciones en la sección de configuración.
4. Ejecuta la aplicación en un emulador o dispositivo Android.

### 5. Contribuciones

¡Las contribuciones son bienvenidas! Si deseas mejorar la aplicación, abre un problema o envía una solicitud de extracción.

### 6. Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo `LICENSE` para obtener más detalles.

### 7. Contacto

Si tienes preguntas o comentarios, no dudes en ponerte en contacto conmigo a través de [correo electrónico](mailto:pvaraque@gmail.com).
