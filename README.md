# BOTICA
Sistema de inventario

##  Stack Tecnológico

*   **Backend:** Java 21 / Spring Boot [3.5.4]
*   **Frontend:** Thymeleaf (Motor de plantillas) / HTML5
*   **Estilos:** Tailwind CSS
*   **Gestor de dependencias (Java):** Maven
*   **Gestor de dependencias (CSS):** npm (Node.js)

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