# 🚀 INICIO RÁPIDO - Arquitectura Hexagonal

## ✅ ¿Qué se ha creado?

Has migrado exitosamente tu aplicación de **arquitectura por CAPAS** a **arquitectura HEXAGONAL**.

### 📁 Estructura completa:

```
HEXAGONAL/
├── domain/                    # 🟦 Dominio (Núcleo)
│   ├── model/
│   │   └── Saldo.java
│   ├── exception/
│   │   └── SaldoNoEncontradoException.java
│   └── port/
│       ├── in/
│       │   └── ConsultarSaldoUseCase.java
│       └── out/
│           ├── SaldoRepositoryPort.java
│           └── SaldoCachePort.java
│
├── application/               # 🟨 Aplicación (Orquestación)
│   └── usecase/
│       └── ConsultarSaldoUseCaseImpl.java
│
└── infrastructure/            # 🟩 Infraestructura (Adaptadores)
    ├── adapter/
    │   ├── in/rest/
    │   │   ├── SaldoController.java
    │   │   └── GlobalExceptionHandler.java
    │   └── out/
    │       ├── persistence/
    │       │   ├── SaldoEntity.java
    │       │   ├── SaldoR2dbcRepository.java
    │       │   └── SaldoRepositoryAdapter.java
    │       └── cache/
    │           └── RedisCacheAdapter.java
    └── config/
        └── RedisConfig.java
```

---

## 🎯 Próximos Pasos

### 1️⃣ **HOY** - Entender la estructura (30 min)

Lee estos archivos en orden:

1. **`HEXAGONAL/README.md`** - Conceptos fundamentales
2. **`COMPARACION_ARQUITECTURAS.md`** - Diferencias CAPAS vs HEXAGONAL
3. **`DIAGRAMAS_HEXAGONAL.md`** - Diagramas visuales del flujo

### 2️⃣ **HOY** - Ejecutar la aplicación (15 min)

```bash
# 1. Inicia las dependencias (Docker)
docker-compose up -d

# 2. Ejecuta la aplicación
.\mvnw.cmd spring-boot:run

# 3. Verifica que esté funcionando
curl http://localhost:8080/actuator/health
```

### 3️⃣ **MAÑANA** - Probar el endpoint (30 min)

```bash
# 1. Obtén un token JWT
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"user\",\"password\":\"password\"}"

# 2. Consulta un saldo (usa el token del paso anterior)
curl -X GET http://localhost:8080/api/v1/saldos/123456 \
  -H "Authorization: Bearer {TOKEN_AQUI}"

# 3. Revisa las métricas
# http://localhost:8080/actuator/metrics
# http://localhost:8080/actuator/circuitbreakers
```

### 4️⃣ **ESTA SEMANA** - Primer ejercicio (2-3 horas)

Implementa el caso de uso "Crear Saldo":

1. Lee **`EJERCICIOS_HEXAGONAL.md`** - Ejercicio 1
2. Crea los archivos indicados paso a paso
3. Prueba el endpoint POST
4. Haz commit de tu progreso

### 5️⃣ **PRÓXIMA SEMANA** - Testing (2-3 horas)

1. Lee **`EJERCICIOS_HEXAGONAL.md`** - Ejercicio 4
2. Crea tests unitarios con mocks
3. Ejecuta los tests: `.\mvnw.cmd test`
4. Revisa la cobertura de código

---

## 📚 Documentación Disponible

| Archivo | Contenido | Tiempo de lectura |
|---------|-----------|-------------------|
| **`HEXAGONAL/README.md`** | Guía completa de arquitectura hexagonal | 15 min |
| **`COMPARACION_ARQUITECTURAS.md`** | Comparación CAPAS vs HEXAGONAL | 10 min |
| **`DIAGRAMAS_HEXAGONAL.md`** | Diagramas visuales y flujos | 10 min |
| **`RESUMEN_HEXAGONAL.md`** | Resumen ejecutivo | 5 min |
| **`EJERCICIOS_HEXAGONAL.md`** | 6 ejercicios prácticos | Variable |
| **`INICIO_RAPIDO.md`** | Este archivo | 3 min |

---

## 🔧 Solución de Problemas Comunes

### ❌ Error: "Could not autowire. No beans of 'SaldoRepositoryPort'"

**Causa**: Spring no detecta los adaptadores como beans.

**Solución**:
1. Verifica que `SaldoRepositoryAdapter` tenga `@Component`
2. Verifica que `RedisCacheAdapter` tenga `@Component`
3. Ejecuta: `.\mvnw.cmd clean compile`

### ❌ Error: "WeakKeyException: The specified key byte array is 88 bits"

**Causa**: La clave JWT es muy corta (menos de 256 bits).

**Solución**: En `application.properties`:
```properties
jwt.secret=miClaveSecretaSuperSeguraQueDebeTenerAlMenos32Caracteres123
```

### ❌ Error: "Connection refused" al intentar conectar a Redis/PostgreSQL

**Causa**: Docker no está corriendo o los contenedores no están iniciados.

**Solución**:
```bash
docker-compose up -d
docker-compose ps  # Verificar que estén corriendo
```

### ❌ La aplicación compila pero no funciona el endpoint

**Causa**: Es probable que estés llamando al endpoint antiguo `/saldos` en lugar del nuevo `/api/v1/saldos`.

**Solución**: Usa la URL correcta: `http://localhost:8080/api/v1/saldos/{cuenta}`

---

## 🎓 Conceptos Clave - Cheat Sheet

### 🔵 PUERTO (Port)
```java
// Es una INTERFAZ definida por el DOMINIO
public interface ConsultarSaldoUseCase {
    Mono<Saldo> consultarSaldo(String cuenta);
}
```

### 🟢 ADAPTADOR (Adapter)
```java
// Es una IMPLEMENTACIÓN en INFRAESTRUCTURA
@Service
public class ConsultarSaldoUseCaseImpl implements ConsultarSaldoUseCase {
    // Implementación...
}
```

### 🎯 Regla de Oro
> **El DOMINIO define las interfaces (puertos).**  
> **La INFRAESTRUCTURA las implementa (adaptadores).**  
> **El dominio NUNCA depende de la infraestructura.**

---

## 📊 Estado del Proyecto

### ✅ Completado
- [x] Estructura hexagonal creada
- [x] Modelo de dominio puro (Saldo)
- [x] Puertos de entrada y salida definidos
- [x] Caso de uso "Consultar Saldo" implementado
- [x] Adaptadores de BD (R2DBC) y Cache (Redis)
- [x] Resiliencia completa (Timeout, Retry, Circuit Breaker, Fallback)
- [x] Documentación exhaustiva

### 📋 Pendiente (Ejercicios para ti)
- [ ] Caso de uso "Crear Saldo"
- [ ] Caso de uso "Actualizar Saldo"
- [ ] Caso de uso "Eliminar Saldo"
- [ ] Validaciones en el modelo de dominio
- [ ] Tests unitarios
- [ ] Tests de integración
- [ ] Adaptador de cache alternativo (Caffeine)
- [ ] Eventos de dominio

---

## 🏆 Objetivo de Aprendizaje

Al completar los ejercicios habrás dominado:

1. ✅ **Arquitectura Hexagonal** (Puertos y Adaptadores)
2. ✅ **Principios SOLID** aplicados
3. ✅ **Domain-Driven Design** básico
4. ✅ **Testing** con mocks y sin dependencias externas
5. ✅ **Resiliencia** con Resilience4j
6. ✅ **Programación Reactiva** con Reactor

---

## 💡 Tips de Productividad

### Para estudiar:
1. **No leas todo de una vez** - Lee por secciones
2. **Ejecuta el código mientras lees** - Aprende haciendo
3. **Dibuja los diagramas** - La visualización ayuda
4. **Haz preguntas específicas** - "¿Por qué X en lugar de Y?"

### Para desarrollar:
1. **Commits pequeños y frecuentes** - Fácil de revertir si algo falla
2. **Un ejercicio a la vez** - No te abrumes
3. **Lee los tests como documentación** - Te muestran cómo usar el código
4. **Usa los logs** - Te ayudan a entender el flujo

---

## 📞 ¿Necesitas Ayuda?

### Preguntas frecuentes:

**P: ¿Por qué separar Entity de Domain Model?**  
R: La Entity tiene anotaciones de BD (@Table, @Id). El Domain Model es puro, sin dependencias de frameworks.

**P: ¿Por qué usar interfaces (puertos)?**  
R: Para aplicar Dependency Inversion. El dominio define QUÉ necesita, no CÓMO lo hace.

**P: ¿No es más código que CAPAS?**  
R: Sí, pero es más mantenible, testeable y flexible a largo plazo.

**P: ¿Cuándo NO usar hexagonal?**  
R: En proyectos muy pequeños (< 5 clases), POCs rápidos, o scripts simples.

---

## 🎯 Checklist de Hoy

- [ ] Leí `HEXAGONAL/README.md`
- [ ] Leí `COMPARACION_ARQUITECTURAS.md`
- [ ] Ejecuté la aplicación con `mvnw spring-boot:run`
- [ ] Probé el endpoint `/actuator/health`
- [ ] Entiendo qué es un Puerto
- [ ] Entiendo qué es un Adaptador
- [ ] Entiendo el flujo: Controller → UseCase → Repository

---

## 🚀 ¡Comienza Ya!

```bash
# 1. Abre la documentación principal
code HEXAGONAL/README.md

# 2. Abre el primer ejercicio
code EJERCICIOS_HEXAGONAL.md

# 3. ¡A programar!
```

---

**🎉 ¡Todo está listo! Es hora de que conviertas el conocimiento en práctica.**

**📖 Siguiente paso**: Abre `HEXAGONAL/README.md` y empieza a leer.

---

_Última actualización: 2026-02-06_  
_Stack: Spring Boot 3.x + WebFlux + R2DBC + Redis + Resilience4j + Hexagonal Architecture_
