# Descripcion del proyecto backend

## 1. Proposito

Este documento describe la parte backend del sistema de cine, su proposito, su estructura interna y su relacion con la base de datos y el frontend.

## 2. Visión general

El backend esta desarrollado con Spring Boot y actua como el centro de la logica de negocio de la plataforma. Su responsabilidad es recibir solicitudes desde el frontend, validar datos, ejecutar reglas de negocio y persistir la informacion en PostgreSQL.

La aplicacion no esta dividida en microservicios. En su lugar, utiliza una arquitectura monolitica modular, organizada por dominios funcionales para mantener separadas las responsabilidades del sistema.

## 3. Responsabilidades principales

El backend se encarga de:

- autenticacion y autorizacion con JWT,
- gestion de usuarios y roles,
- administracion de peliculas, estrenos, salas y funciones,
- venta de boletos y asignacion de asientos,
- compra de dulceria y combos,
- generacion de codigos QR,
- envio de correos de confirmacion,
- integracion con servicios externos como SendGrid y Cloudinary.

## 4. Estructura interna

La organizacion del backend sigue el patron controlador-servicio-repositorio:

- Los controladores exponen los endpoints REST.
- Los servicios contienen la logica principal del negocio.
- Los repositorios acceden a la base de datos mediante JPA.
- Los modelos representan las entidades persistentes.
- Los DTOs permiten transportar datos sin exponer directamente el modelo interno.

## 5. Dominios funcionales

### 5.1 Usuarios y seguridad

Este dominio cubre el registro, el login, la validacion de tokens y la proteccion de rutas segun el rol del usuario.

### 5.2 Cartelera y funciones

Este dominio administra las peliculas disponibles, las salas, los horarios y la relacion entre pelicula y funcion.

### 5.3 Compra de boletos

Este dominio registra las compras de entradas, asigna asientos y genera el comprobante de compra con QR.

### 5.4 Compra de dulceria

Este dominio controla los productos, combos, el detalle de la compra y el envio de la notificacion por correo.

### 5.5 Administracion

Este dominio agrupa las operaciones de mantenimiento y consulta utilizadas por el panel administrativo.

## 6. Persistencia

La persistencia se realiza en PostgreSQL mediante entidades JPA. La base de datos contiene la informacion operativa del sistema, incluyendo usuarios, peliculas, funciones, compras, productos, combos y tablas complementarias.

## 7. Integraciones externas

El backend consume servicios externos para resolver necesidades especificas:

- SendGrid para correo,
- Cloudinary para imagenes,
- PostgreSQL para almacenamiento principal.

## 8. Relacion con el frontend

El backend expone una API REST que es consumida por el frontend. Toda accion de usuario, como iniciar sesion, comprar boletos o adquirir dulceria, se traduce en una peticion HTTP hacia este proyecto.

## 9. Conclusion

El backend es la capa central del sistema. Su diseño busca mantener la logica de negocio controlada, la persistencia organizada y las integraciones desacopladas, permitiendo que el sistema crezca sin perder claridad estructural.