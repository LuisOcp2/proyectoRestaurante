# Guía de Contribución - Proyecto Restaurante

¡Bienvenidos al equipo! Para mantener el código organizado y funcional, seguiremos este marco de trabajo.

## 1. El Flujo de Trabajo (Gitflow Simplificado)

Utilizaremos tres tipos de ramas:

- **`master`**: Código en producción. Nadie sube cambios directamente aquí.
- **`desarrollo`**: Rama de integración. Aquí se unen todas las nuevas funciones.
- **`feature/nombre-tarea`**: Ramas personales para trabajar. 
  - Ejemplo: `feature/ajuste-usuario-luis`, `feature/vista-pedidos-angie`.

### Pasos para realizar una tarea:
1. Asegúrate de estar en `desarrollo` y tener lo último: `git pull origin desarrollo`.
2. Crea tu rama: `git checkout -b feature/mi-tarea`.
3. Desarrolla y haz commits descriptivos.
4. Sube tu rama: `git push origin feature/mi-tarea`.
5. Abre un **Pull Request (PR)** en GitHub hacia la rama `desarrollo`.

## 2. Revisiones de Código
- Al abrir un PR, etiqueta a un compañero para que revise tu código.
- No se puede hacer merge a `desarrollo` sin al menos una aprobación.

## 3. Estándares de Código
- Usar **Español (Latino)** para nombres de métodos y variables de negocio.
- Mantener la arquitectura MVC + DAO.
- No subir archivos binarios pesados o carpetas de compilación (`target/`).

---
*Cualquier duda, consultar con Luis (Architect/Lead).*
