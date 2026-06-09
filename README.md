# GymLabs - Backend API ⚙️

Este repositorio contiene el **Backend (API REST)** del sistema de gestión para gimnasios GymLabs. Está construido utilizando arquitectura empresarial robusta para proveer alta disponibilidad, escalabilidad y seguridad a la plataforma.

## ✨ Características Principales

*   🚀 **Arquitectura RESTful:** Endpoints estructurados y limpios para la comunicación eficiente con el Frontend.
*   📊 **Dashboard de Métricas:** Algoritmos de agregación nativos en base de datos para calcular ingresos y adquisición de clientes sin sobrecargar la red.
*   👥 **Gestión de Entidades (CRUD):** Control completo sobre Clientes, Planes, Membresías, Pagos y Personal.
*   ☁️ **Cloud-Ready:** Configurado para despliegue sin fricción gracias a la inclusión de `Dockerfile`.
*   🗄️ **Generación de Esquema Automática:** Uso de `ddl-auto` con JPA/Hibernate para migraciones ágiles y automáticas de la base de datos (Data Driven Development).

## 🛠️ Stack Tecnológico

*   **Lenguaje:** [Java 21](https://jdk.java.net/21/)
*   **Framework Principal:** [Spring Boot 3.x](https://spring.io/projects/spring-boot)
*   **Persistencia:** Spring Data JPA / Hibernate
*   **Base de Datos:** [MySQL](https://www.mysql.com/) (Preparado para bases de datos administradas en la nube como Railway)
*   **Gestor de Dependencias:** Maven
*   **Contenedores:** Docker

## 🚀 Despliegue en la Nube (Cloud)

Este proyecto está optimizado para su despliegue gratuito o económico en **Render.com**.
1. Vincula tu repositorio a **Render** creando un nuevo `Web Service`.
2. El sistema detectará automáticamente el archivo `Dockerfile` en la raíz.
3. El puerto `8081` se encuentra expuesto nativamente en la imagen para su consumo.
4. Asegúrate de actualizar tus credenciales de MySQL de la nube (ej. Railway) en `src/main/resources/application.properties` antes del despliegue.

## 💻 Instalación Local

Para correr el servidor en tu computadora de manera local, sigue estos pasos:

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/ELDSC/GymLabs-BackEnd.git
   cd GymLabs-BackEnd
   ```

2. **Configurar tu base de datos:**
   Abre el archivo `src/main/resources/application.properties` e inserta tus credenciales locales o de la nube.

3. **Ejecutar el servidor:**
   Utiliza el wrapper de Maven que viene incluido (no necesitas tener Maven instalado):
   *En Windows (PowerShell/CMD):*
   ```bash
   .\mvnw.cmd spring-boot:run
   ```
   *En Linux/Mac:*
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Validación:**
   El backend iniciará por defecto en el puerto `8081`. Puedes comprobar el estado enviando una petición GET a `http://localhost:8081/api/clientes`.

## 📁 Estructura del Proyecto

```text
src/main/java/com/GYMLABS/proyecto/
├── config/           # Configuraciones globales (Inicialización de datos, Seguridad)
├── controller/       # Controladores REST (Exposición de endpoints /api/...)
├── dto/              # Data Transfer Objects (Ej. ChartData, DashboardDTO)
├── model/            # Entidades JPA (Mapeo objeto-relacional con MySQL)
├── repository/       # Interfaces de Spring Data (Consultas JPQL/Nativas)
└── service/          # Lógica de negocio core (Reglas y cálculos)
```

## 👥 Contribuciones
Este proyecto fue diseñado con escalabilidad en mente. Eres libre de hacer un Fork y enviar *Pull Requests* para extender sus funcionalidades.
