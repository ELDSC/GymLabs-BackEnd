# 📖 Contexto y Reglas de Negocio (GymLabs)

## 1. 📖 Glosario del Dominio (Ubiquitous Language)
- **Empresa**: Entidad principal en la arquitectura Multi-Tenant. Cada gimnasio es una Empresa separada (ej. "GymLabs Fit", "Power Gym"). Toda la información está aislada por el `idEmpresa`.
- **Sede**: Sucursal física de una Empresa. Una empresa puede tener múltiples sedes.
- **Personal**: Trabajadores del gimnasio (recepcionistas, administradores). Pertenecen a una Sede.
- **Rol**: Nivel de acceso del Personal en el sistema (ej. `ROLE_ADMIN`, `ROLE_RECEPCIONISTA`).
- **Cliente**: Persona que asiste al gimnasio. Su registro es único, pero su acceso depende de su Membresía.
- **Plan**: Catálogo de ofertas del gimnasio (ej. "Plan Mensual", "Plan Anual"). Define el precio, duración en días y beneficios.
- **Membresía**: El contrato activo de un Cliente basado en un Plan. Tiene estados (`ACTIVA`, `VENCIDA`, `CANCELADA`) y fechas de inicio y fin.
- **Pago / Boleta**: Transacción financiera vinculada a la compra o renovación de una Membresía.
- **Notificación (Alerta)**: Sistema para informar sobre vencimientos (por WhatsApp o correo).

## 2. 🏛️ Arquitectura del Sistema
- **Backend (API REST)**: Desarrollado en **Spring Boot 3 (Java 21)**.
  - **ORM**: JPA / Hibernate conectado a MySQL.
  - **Seguridad**: Spring Security con autenticación basada en JWT. El token se envía vía cookies (`HttpOnly, Secure, SameSite=None`) y como fallback mediante el header `Authorization: Bearer <token>`.
  - **Monitoreo**: Spring Boot Actuator expone `/actuator/prometheus` scrapeado por una instancia local de **Prometheus**, el cual visualiza métricas de la JVM en **Grafana**.
- **Frontend (UI)**: Desarrollado en **Next.js 14+ (App Router)** usando TypeScript.
  - **Estilos**: TailwindCSS y componentes de Shadcn UI.
  - **Manejo de Estado**: React Context API (`AuthContext`) para la sesión global y persistencia en `localStorage`.

## 3. ⚖️ Reglas de Negocio Clave
### Aislamiento Multi-Tenant
- Todas las tablas principales (`Personal`, `Cliente`, `Plan`, `Membresia`) están ligadas indirecta o directamente a una `Empresa`. 
- Cualquier consulta (GET, POST, PUT, DELETE) filtra por el `empresaId` del usuario autenticado. Un recepcionista de la "Empresa A" jamás podrá ver ni modificar datos de la "Empresa B".

### Soft Delete (Borrado Lógico)
- El **Personal** y los **Clientes** no se borran físicamente de la base de datos (con comando `DELETE`), sino que se marcan con un flag `activo = false` o `estado = CANCELADA/VENCIDA`.
- *Razón*: Mantener la integridad referencial. Si se borra un empleado, los pagos que ese empleado registró no deben romperse.

### Unicidad de Entidades
- No puede existir más de un `Personal` activo con el mismo **DNI** o **Correo Electrónico**. Al intentar registrar un duplicado, la API responde con un `409 Conflict`.
- De igual forma con los `Clientes`.

### Estado de la Membresía
- La Membresía de un cliente dicta si tiene permitido el ingreso.
- Se actualiza automáticamente a `VENCIDA` cuando la `fechaFin` expira.

## 4. 🔒 Modelo de Permisos y Autorización
- El acceso se controla mediante Spring Security (`SecurityConfig.java`). Las rutas `/api/auth/login` son públicas, mientras que las demás requieren autenticación.
- En el **Frontend** (`AuthContext.tsx`), se realiza una redirección estricta:
  - Si el usuario es `ROLE_SUPERADMIN`, es redirigido a `/superadmin`.
  - Si el usuario es `ROLE_ADMIN` o recepcionista, es redirigido a `/dashboard`. No pueden acceder a las rutas del superadministrador.
