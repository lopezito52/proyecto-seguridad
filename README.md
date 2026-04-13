# 🔐 Taller de Seguridad en Microservicios

## TLS, mTLS y JWT con Spring Boot

---

## 📌 Descripción General

Este proyecto implementa un sistema de microservicios seguros utilizando:

* 🔐 **TLS** para comunicación segura con Backend A
* 🔐 **mTLS** para autenticación mutua con Backend B
* 🎫 **JWT** para autenticación y autorización de usuarios

La arquitectura está compuesta por:

* **MS Cliente**: Orquestador de las solicitudes
* **Backend A**: Autenticación y generación de tokens (TLS)
* **Backend B**: Acceso a datos sensibles (mTLS)

---

## 🏗️ Arquitectura

El sistema sigue una arquitectura basada en cliente-orquestador:

```
Cliente (Postman / Navegador)
        ↓
   MS Cliente
     ↓     ↓
Backend A  Backend B
 (TLS)       (mTLS)
```

> 📎 El diagrama de arquitectura se encuentra adjunto en el documento.

---

## 📜 Certificados generados por la CA

Se implementó una Autoridad Certificadora (CA) para firmar los certificados de los servicios.

### ✔️ Proceso:

1. Generación de keystore (.jks)
2. Creación de CSR (Certificate Signing Request)
3. Firma por la CA
4. Importación del certificado firmado

### 📁 Archivos utilizados:

* `backendA.jks`
* `backendB.jks`
* `cliente.jks`
* Certificados `.crt` firmados por la CA

---

## 🔐 Configuración TLS

Backend A utiliza TLS para asegurar la comunicación.

### ✔️ Características:

* Solo el servidor presenta certificado
* El cliente valida la identidad del servidor
* Canal cifrado mediante HTTPS

### 🔧 Configuración:

* Puerto seguro (ej: 8443)
* Uso de keystore (`.jks`)
* Validación del certificado del servidor

---

## 🔐 Configuración mTLS

Backend B implementa autenticación mutua.

### ✔️ Características:

* Cliente y servidor presentan certificados
* Ambos validan la identidad del otro
* Mayor nivel de seguridad

### 🔧 Configuración:

* Truststore y keystore configurados
* Validación del certificado del cliente
* Uso de `RestTemplate` con configuración SSL

---

## 🎫 Flujo de Autenticación con JWT

El sistema utiliza JWT para autenticación y control de acceso.

### 🔄 Flujo:

1. El usuario realiza login en el MS Cliente
2. El cliente solicita token a Backend A
3. Backend A valida credenciales y genera JWT
4. El cliente usa el token para consumir servicios
5. Backends validan el token mediante un filtro JWT

---

# ⚙️ Explicación Técnica

---

## 🔐 ¿Cómo se establece la confianza?

La confianza se establece mediante una **Autoridad Certificadora (CA)**:

* La CA firma los certificados de todos los servicios
* Cada servicio confía en la CA
* Al validar certificados, se verifica que fueron firmados por la misma CA

👉 Esto permite garantizar identidad y evitar ataques tipo *man-in-the-middle*

---

## 🔍 Diferencias entre TLS, mTLS y JWT

---

### 🔐 TLS (Transport Layer Security)

* Protege la comunicación (cifrado)
* Solo el servidor se autentica
* Usado en Backend A

✔️ Seguridad en el canal
❌ No autentica al cliente

---

### 🔐 mTLS (Mutual TLS)

* Cliente y servidor se autentican mutuamente
* Ambos presentan certificados
* Usado en Backend B

✔️ Máxima seguridad
✔️ Identidad en ambos extremos

---

### 🎫 JWT (JSON Web Token)

* Mecanismo de autenticación y autorización
* Contiene información del usuario (claims)
* Se envía en el header `Authorization`

✔️ Control de acceso
✔️ Stateless (sin sesión)

---

## 🧠 Integración de Seguridad

El sistema combina tres niveles de seguridad:

| Capa         | Tecnología | Función                 |
| ------------ | ---------- | ----------------------- |
| Red          | TLS / mTLS | Cifrado y autenticación |
| Aplicación   | JWT        | Autenticación y roles   |
| Arquitectura | MS Cliente | Orquestación            |

---

# 🧪 Evidencia de Pruebas

Se realizaron las siguientes pruebas (ver video):

* ❌ Acceso sin HTTPS → rechazado
* ❌ Acceso sin token → rechazado
* ❌ Token inválido → rechazado
* ❌ Cliente sin certificado (mTLS) → rechazado
* ✅ Acceso válido con TLS + JWT → exitoso
* ✅ Acceso válido con mTLS + JWT → exitoso

---

# 🚀 Conclusiones

* Se logró implementar correctamente TLS, mTLS y JWT
* La arquitectura garantiza seguridad en múltiples niveles
* El sistema previene accesos no autorizados
* Se evidencia la importancia de la confianza basada en certificados

---

# 👥 Autores

* Samuel Lopez, Nicolas Urrea y Samuel Acero

---
