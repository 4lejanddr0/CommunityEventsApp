# 🎉 Community Events App
**Gestión de eventos comunitarios con autenticación, Firestore, Jetpack Compose y arquitectura MVVM.**

![Kotlin](https://img.shields.io/badge/Kotlin-1.9-blueviolet)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Mobile%20UI-brightgreen)
![Firebase](https://img.shields.io/badge/Firebase-Backend-orange)
![Architecture](https://img.shields.io/badge/MVVM-Architecture-ff69b4)
![Status](https://img.shields.io/badge/Status-Completed-success)

---

## 📌 Descripción general
**Community Events App** es una aplicación móvil creada para gestionar eventos comunitarios, permitiendo a los usuarios:

- Autenticarse con **correo/contraseña** y **Google**
- Crear, editar y eliminar eventos
- Ver detalles completos de cada evento
- Comentar únicamente en eventos pasados
- Recibir **notificaciones simuladas**
- Guardar información en tiempo real usando Firebase Firestore

Desarrollada completamente en **Kotlin + Jetpack Compose**, con arquitectura **MVVM**, **StateFlow** e **inyección de dependencias con Hilt**.

---

## 🔗 Enlaces del Proyecto

### 📋 Tablero de Trello
Gestión del cronograma, estructura de tareas y control de avances del proyecto:
👉 https://trello.com/b/knmhUNWk/communityevents

### 🎨 Prototipo de Figma
Diseño visual, estructura de pantallas y flujo de usuario del prototipo de la aplicación:
👉 **https://LINK-DE-FIGMA-AQUI**

---

## 👨‍👩‍👦‍👦 Integrantes del equipo

| Nombre | Carnet | Contacto |
|--------|------|-----------|
| Jesús Alejandro Campos Landaverde | CL212345 | 
| Miembro 2 | Documentación / QA | correo@udb.edu.sv |
| Miembro 3 | Diseño UI / Pruebas | correo@udb.edu.sv |
| Miembro 4 | Apoyo en análisis | correo@udb.edu.sv |

---

## 🚀 Funcionalidades principales

### 🔐 Autenticación
- Login con correo y contraseña  
- Login con Google  
- Registro de usuarios  
- Persistencia de sesión  
- Manejo de errores validado

### 📅 Gestión completa de eventos (CRUD)
- Crear eventos  
- Listado general  
- Detalle del evento  
- Edición  
- Eliminación  
- Validación de campos  
- Almacenamiento en Firestore

### 💬 Comentarios en eventos pasados
- Solo se puede comentar si el evento ya ocurrió  
- Comentarios ordenados por fecha  
- Relación Evento → Comentarios  
- Vista integrada en la pantalla de detalle

### 🔔 Notificaciones simuladas
- Recordatorios configurados en el ViewModel  
- Notificaciones internas sin FCM  
- Alertas sobre eventos próximos

---

## 🛠️ Tecnologías utilizadas

### **Frontend**
- Kotlin  
- Jetpack Compose  
- Material 3  
- Navigation Compose  

### **Backend**
- Firebase Authentication  
- Firebase Firestore  
- Firebase Storage  

### **Arquitectura**
- MVVM  
- Hilt (Dependency Injection)  
- StateFlow  
- Repositorios  
- Clean UI States  

---

## ☁️ Configuración de Firebase

### 1️⃣ Crear proyecto en Firebase  
### 2️⃣ Activar servicios:
- Authentication  
- Firestore Database  
- Storage  

### 3️⃣ Descargar `google-services.json` y agregarlo en:
app/google-services.json


### 4️⃣ Activar Google Sign-In  
Agregar SHA-1 y SHA-256 en Firebase.

### 5️⃣ Verificar `default_web_client_id` en:
app/src/main/res/values/strings.xml

## ▶️ Ejecución del proyecto
Clonar el repositorio:
git clone https://github.com/4lejanddr0/CommunityEventsApp.git

---

## 📄 Licencia

Este proyecto combina dos tipos de licencias para cubrir distintos componentes:

### 🧑‍💻 1. Licencia para el código fuente – MIT License
Todo el código desarrollado en Kotlin (incluyendo vistas, ViewModels, repositorios y utilidades) se distribuye bajo la licencia **MIT**, permitiendo su uso, modificación y redistribución con atribución al autor original.

### 📘 2. Licencia para la documentación – Creative Commons BY-NC 4.0
Los documentos, imágenes, textos explicativos y contenido académico del proyecto están protegidos bajo la licencia  
**Creative Commons Atribución–No Comercial 4.0 Internacional (CC BY-NC 4.0)**.

Esta licencia permite copiar y adaptar el contenido siempre que:

- Se brinde crédito al autor original (BY)
- No se utilice con fines comerciales (NC)

**Más información:**  
🔗 https://creativecommons.org/licenses/by-nc/4.0/

---

