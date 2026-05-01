# BOTICA
Sistema de inventario

##  Stack Tecnológico

*   **Backend:** Java 21 / Spring Boot [3.5.4]
*   **Frontend:** Thymeleaf (Motor de plantillas) / HTML5
*   **Estilos:** Tailwind CSS
*   **Gestor de dependencias (Java):** Maven
*   **Gestor de dependencias (CSS):** npm (Node.js)

Dependencias
* JPA
* spring web
* spring security
* thymeleaf
* devtools
* mysql
* lombok

##  Requisitos Previos

Dado que el equipo trabaja con diferentes IDEs (IntelliJ, VS Code, NetBeans), el proyecto está configurado para ser **100% agnóstico al editor**. El jefe del proyecto es `pom.xml`.

Para poder ejecutar este proyecto localmente, necesitas tener instalado:

1.  [Java Development Kit (JDK) 21](https://adoptium.net/)
2.  [Node.js y npm](https://nodejs.org/) (Necesario para compilar Tailwind CSS)
3.  [Git](https://git-scm.com/)

## Cómo iniciar el proyecto localmente

Sigue estos pasos para clonar y levantar el entorno de desarrollo por primera vez:

### 1. Clonar el repositorio e instalar dependencias

Abre tu terminal y ejecuta:
```bash
# Clonar el proyecto
git clone [URL_DEL_REPOSITORIO]

# Entrar a la carpeta
cd [NOMBRE_DE_LA_CARPETA]

# Instalar las dependencias de Tailwind CSS (¡Paso obligatorio!)
npm install

mi-proyecto/
├── pom.xml                     <-- Dependencias de Java (Spring, Thymeleaf, etc.)
├── package.json                <-- Dependencias de Node (Tailwind, PostCSS)
├── tailwind.config.js          <-- Configuración donde le dices a Tailwind dónde buscar tus HTML
├── src/
│   ├── main/
│   │   ├── java/com/miempresa/app/
│   │   │   ├── MiProyectoApplication.java  <-- Punto de entrada de Spring Boot
│   │   │   ├── config/             <-- Clases de configuración (Spring Security va aquí)
│   │   │   ├── controller/         <-- Controladores web que devuelven las vistas Thymeleaf
│   │   │   ├── model/              <-- Clases que representan tus tablas de base de datos
│   │   │   ├── repository/         <-- Interfaces para consultas a la base de datos (JPA)
│   │   │   └── service/            <-- Lógica de negocio de tu aplicación
│   │   │
│   │   └── resources/
│   │       ├── application.properties  <-- Configuración de Spring (Conexión a BD, puertos)
│   │       ├── static/             <-- Archivos públicos a los que el navegador tiene acceso
│   │       │   ├── css/
│   │       │   │   ├── input.css   <-- Archivo origen con las directivas de Tailwind (@tailwind base...)
│   │       │   │   └── output.css  <-- Archivo generado por Tailwind (NO lo tocas a mano)
│   │       │   ├── js/
│   │       │   └── images/
│   │       │
│   │       └── templates/          <-- Tus archivos HTML procesados por Thymeleaf
│   │           ├── index.html
│   │           ├── login.html
│   │           └── fragments/      <-- Trozos de HTML reutilizables (navbar, footer)
│   │
│   └── test/                       <-- Pruebas unitarias y de integración
