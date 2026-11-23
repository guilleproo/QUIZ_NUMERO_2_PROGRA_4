Sistema de Gestión de Departamentos y Empleados

Aplicación web desarrollada con Spring Boot, Spring Data JPA, Spring Security y Thymeleaf para la administración de departamentos, empleados y control de presupuesto de planilla, con control de acceso basado en roles (ADMIN y USER).

Información General

Estudiante: GUILLERMO ARTURO CORRALES GIAMMATTEI

Versión de Java: Java 21


Motor de Base de Datos: H2

Cómo Ejecutar el Proyecto

Prerrequisitos

JDK 17 

Maven 3.8+ (o usar el wrapper ./mvnw incluido en el proyecto).

Pasos de ejecución

1- Clonar el repositorio
2- Compilar y ejecutar la aplicacion desde consola
3- Acceder en el navegador a LOCALHOST:8080
4- Ingresar con los usuarios de prueba

**************************************************************************

Archivos Creados o Modificados por Requerimiento (R1 - R7)

R1 – Entidades y Modelo

Departamento.java: entidad con datos del departamento, presupuesto y relación con empleados.

Empleado.java: entidad con datos personales, salario, fecha de ingreso, puesto y departamento.

Puesto.java: enum con los puestos disponibles.

R2 – Repositorios (JPA / JPQL, sin SQL nativo)

DepartamentoRepository.java: consultas básicas y CRUD de departamentos.

EmpleadoRepository.java: consultas para filtrar por departamento, validar datos únicos y sumar salarios.

R3 – Servicios

DepartamentoService.java: cálculos de cantidad de empleados, planilla, presupuesto disponible y validación al eliminar.

EmpleadoService.java: lógica de guardado/edición y validación del presupuesto disponible del departamento.

R4 – Controladores

DepartamentoController.java: rutas para listar, crear, editar y eliminar departamentos.

EmpleadoController.java: rutas para listar (con filtro por departamento), crear, editar y eliminar empleados.

LoginController.java: manejo de /login y redirección inicial.

UsuarioController.java: vista de usuarios para el administrador.

R5 – Vistas Thymeleaf y Bootstrap

fragments/navbar.html: barra de navegación compartida y botón de salir.

login.html: pantalla de inicio de sesión.

departamentos/lista.html y departamentos/formulario.html: listado y formulario de departamentos.

empleados/lista.html y empleados/formulario.html: listado con filtro y formulario de empleados.

usuarios/lista.html: tabla de usuarios del sistema.

R6 – Seguridad (Spring Security)

SecurityConfig.java: configuración de rutas por rol (ADMIN y USER), usuarios en memoria con BCrypt, login y logout.

Vistas (navbar.html, departamentos/lista.html, empleados/lista.html): etiquetas sec:authorize para ocultar botones a usuarios sin permiso.

R7 – Configuración y Documentación

pom.xml: dependencias de Web, JPA, Thymeleaf, Security y Validación.

application.properties: configuración de la base de datos y JPA.

README.md: documentación del proyecto.
