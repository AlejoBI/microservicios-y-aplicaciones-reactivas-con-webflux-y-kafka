# 🏗️ ARQUITECTURA HEXAGONAL - Guía de Aprendizaje

## 📚 ¿Qué es la Arquitectura Hexagonal?

También conocida como **Puertos y Adaptadores**, es un patrón arquitectónico que:

- ✅ **Desacopla** el negocio de la tecnología
- ✅ **Facilita el testing** (puedes testear sin BD, sin HTTP, etc.)
- ✅ **Permite cambiar tecnologías** sin tocar el dominio
- ✅ **Aplica principios SOLID** en toda la arquitectura

---

## 🎯 Estructura del Proyecto

```
HEXAGONAL/
│
├── 🟦 domain/                          # NÚCLEO (Core) - Lógica de negocio pura
│   ├── model/                          # Entidades de dominio
│   │   └── Saldo.java                  # Modelo inmutable, sin dependencias
│   │
│   ├── exception/                      # Excepciones de negocio
│   │   └── SaldoNoEncontradoException.java
│   │
│   └── port/                           # PUERTOS (interfaces)
│       ├── in/                         # Puertos de entrada (casos de uso)
│       │   └── ConsultarSaldoUseCase.java    # "QUÉ hace la app"
│       │
│       └── out/                        # Puertos de salida (dependencias)
│           ├── SaldoRepositoryPort.java      # "CÓMO accedo a BD"
│           └── SaldoCachePort.java           # "CÓMO accedo a cache"
│
├── 🟨 application/                     # CAPA DE APLICACIÓN - Orquestación
│   └── usecase/
│       └── ConsultarSaldoUseCaseImpl.java    # Implementa la lógica del caso de uso
│
└── 🟩 infrastructure/                  # INFRAESTRUCTURA - Detalles técnicos
    ├── adapter/
    │   ├── in/                         # Adaptadores de ENTRADA
    │   │   └── rest/
    │   │       ├── SaldoController.java          # HTTP → Dominio
    │   │       └── GlobalExceptionHandler.java   # Errores → HTTP
    │   │
    │   └── out/                        # Adaptadores de SALIDA
    │       ├── persistence/
    │       │   ├── SaldoEntity.java              # Tabla BD
    │       │   ├── SaldoR2dbcRepository.java     # Spring Data
    │       │   └── SaldoRepositoryAdapter.java   # Puerto → R2DBC
    │       │
    │       └── cache/
    │           └── RedisCacheAdapter.java        # Puerto → Redis
    │
    └── config/
        └── RedisConfig.java            # Configuración técnica
```

---

## 🔄 Flujo de Datos (Request-Response)

```
1. HTTP Request
   ↓
2. 🟩 SaldoController (Infrastructure - Adaptador IN)
   ↓ usa
3. 🟦 ConsultarSaldoUseCase (Domain - Puerto IN)
   ↓ implementado por
4. 🟨 ConsultarSaldoUseCaseImpl (Application)
   ↓ usa
5. 🟦 SaldoCachePort (Domain - Puerto OUT)
   ↓ implementado por
6. 🟩 RedisCacheAdapter (Infrastructure - Adaptador OUT)
   ↓ accede a
7. ⚡ Redis
   ↓ si no hay cache
8. 🟦 SaldoRepositoryPort (Domain - Puerto OUT)
   ↓ implementado por
9. 🟩 SaldoRepositoryAdapter (Infrastructure - Adaptador OUT)
   ↓ accede a
10. 🗄️ PostgreSQL
    ↓
11. Respuesta viaja de vuelta hasta el Controller
```

---

## 🎨 Principios SOLID Aplicados

### 1️⃣ **S - Single Responsibility Principle**
✅ Cada clase tiene UNA sola razón para cambiar:
- `Saldo`: Solo representa el concepto de negocio
- `SaldoController`: Solo maneja HTTP
- `RedisCacheAdapter`: Solo maneja Redis
- `ConsultarSaldoUseCaseImpl`: Solo orquesta la consulta

### 2️⃣ **O - Open/Closed Principle**
✅ Abierto para extensión, cerrado para modificación:
- Puedes agregar un nuevo adaptador (ej: MongoDB) sin modificar el dominio
- Puedes agregar un nuevo caso de uso sin modificar los existentes

### 3️⃣ **L - Liskov Substitution Principle**
✅ Las implementaciones son intercambiables:
- Puedes cambiar `RedisCacheAdapter` por `HazelcastCacheAdapter`
- El caso de uso NO necesita saber qué implementación usa

### 4️⃣ **I - Interface Segregation Principle**
✅ Interfaces específicas y cohesivas:
- `ConsultarSaldoUseCase`: Solo para consultar
- `SaldoCachePort`: Solo operaciones de cache
- `SaldoRepositoryPort`: Solo operaciones de BD

### 5️⃣ **D - Dependency Inversion Principle**
✅ Dependemos de abstracciones, NO de implementaciones:
- El dominio define los puertos (interfaces)
- La infraestructura implementa los puertos
- **El dominio NO depende de la infraestructura** ⭐

---

## 🆚 Comparación: CAPAS vs HEXAGONAL

| Aspecto | CAPAS (Tradicional) | HEXAGONAL |
|---------|---------------------|-----------|
| **Acoplamiento** | Alto (Service depende de Repository concreto) | Bajo (UseCase depende de puerto) |
| **Testing** | Difícil (necesitas BD, Redis, etc.) | Fácil (usas mocks de puertos) |
| **Cambiar tecnología** | Difícil (tocar mucho código) | Fácil (solo cambiar adaptador) |
| **Lógica de negocio** | Mezclada con detalles técnicos | Pura, sin dependencias |
| **Dominio** | `SaldoEntity` (con `@Entity`, `@Table`) | `Saldo` (POJO puro) |

---

## 🧪 Ejemplo de Testing

### Testing en CAPAS (difícil):
```java
@Test
void testConsultarSaldo() {
    // ❌ Necesitas: BD real, Redis real, CircuitBreaker configurado
    SaldoService service = new SaldoService(
        realRepository,     // ❌ Requiere BD
        realCache,          // ❌ Requiere Redis
        circuitBreaker,     // ❌ Configuración compleja
        meterRegistry
    );
}
```

### Testing en HEXAGONAL (fácil):
```java
@Test
void testConsultarSaldo() {
    // ✅ Solo necesitas mocks de puertos
    SaldoRepositoryPort mockRepo = mock(SaldoRepositoryPort.class);
    SaldoCachePort mockCache = mock(SaldoCachePort.class);
    
    // ✅ Test unitario puro, sin dependencias externas
    ConsultarSaldoUseCase useCase = new ConsultarSaldoUseCaseImpl(
        mockRepo, mockCache, circuitBreaker, meterRegistry
    );
    
    // ✅ Defines el comportamiento esperado
    when(mockCache.obtener("123")).thenReturn(Mono.empty());
    when(mockRepo.findByNumeroCuenta("123"))
        .thenReturn(Mono.just(new Saldo("123", 1000.0)));
}
```

---

## 🔌 Puertos vs Adaptadores

### 🔵 PUERTO (Port)
- Es una **INTERFAZ** (abstracción)
- Definida por el **DOMINIO**
- Dice **QUÉ** se necesita
- Ejemplo: `SaldoCachePort`

### 🟢 ADAPTADOR (Adapter)
- Es una **IMPLEMENTACIÓN**
- Definida en **INFRAESTRUCTURA**
- Dice **CÓMO** se hace
- Ejemplo: `RedisCacheAdapter`

```java
// 🔵 PUERTO (en domain/port/out)
public interface SaldoCachePort {
    Mono<Saldo> obtener(String cuenta);
}

// 🟢 ADAPTADOR (en infrastructure/adapter/out)
@Component
public class RedisCacheAdapter implements SaldoCachePort {
    private final ReactiveRedisTemplate<String, Saldo> redis;
    
    @Override
    public Mono<Saldo> obtener(String cuenta) {
        return redis.opsForValue().get(cuenta);
    }
}
```

---

## 🎯 Ventajas de esta Arquitectura

1. ✅ **Independencia de frameworks**: El dominio NO depende de Spring, JPA, etc.
2. ✅ **Testeable**: Testing unitario fácil con mocks
3. ✅ **Flexible**: Cambiar BD sin tocar el dominio
4. ✅ **Mantenible**: Código organizado y desacoplado
5. ✅ **Escalable**: Fácil agregar nuevos casos de uso
6. ✅ **SOLID**: Principios aplicados en cada capa

---

## 🚀 Próximos Pasos

1. ✅ Estructura creada
2. ⏭️ Agregar más casos de uso (Crear Saldo, Actualizar, Eliminar)
3. ⏭️ Agregar tests unitarios
4. ⏭️ Agregar tests de integración
5. ⏭️ Documentar con OpenAPI/Swagger

---

## 📖 Glosario

- **Dominio**: Lógica de negocio pura (core)
- **Puerto**: Interfaz que define una necesidad
- **Adaptador**: Implementación de un puerto
- **Use Case**: Caso de uso, flujo de negocio
- **Entity (Dominio)**: Modelo de negocio inmutable
- **Entity (JPA)**: Representación de tabla en BD
- **DTO**: Data Transfer Object (para HTTP)

---

## 🎓 Recursos de Aprendizaje

- [Hexagonal Architecture - Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Get Your Hands Dirty on Clean Architecture](https://reflectoring.io/book/)
- [SOLID Principles](https://www.baeldung.com/solid-principles)

---

**🎉 ¡Felicidades! Ya tienes una arquitectura hexagonal completa y lista para usar.**
