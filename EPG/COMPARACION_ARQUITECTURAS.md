# 📊 COMPARACIÓN: ARQUITECTURA CAPAS vs HEXAGONAL

## 🔍 Análisis Lado a Lado

### 1️⃣ MODELO DE DOMINIO

#### CAPAS (tradicional):
```java
// ❌ SaldoEntity.java - Mezclado con detalles de BD
@Data                           // ⚠️ Mutable
@Entity                         // ⚠️ Depende de JPA
@Table(name = "saldos")         // ⚠️ Conoce estructura de BD
public class SaldoEntity {
    @Id                         // ⚠️ Anotación de framework
    private Long id;
    private String numeroCuenta;
    private Double monto;
    
    // ❌ Sin lógica de negocio
    // ❌ Getters/Setters automáticos (mutable)
}
```

#### HEXAGONAL:
```java
// ✅ Saldo.java - Dominio puro
@AllArgsConstructor             // ✅ Inmutable por diseño
@Getter
public class Saldo {
    private final String numeroCuenta;  // ✅ final = inmutable
    private final double monto;
    
    // ✅ Lógica de negocio en el dominio
    public boolean tieneFondos() {
        return monto > 0;
    }
    
    public Saldo aplicarDescuento(double porcentaje) {
        double nuevoMonto = monto - (monto * porcentaje / 100);
        return new Saldo(numeroCuenta, nuevoMonto);
    }
}

// ✅ SaldoEntity.java - Solo para persistencia (separado)
@Table(name = "saldos")
public class SaldoEntity {
    // Métodos de conversión:
    public Saldo toDomain() { ... }
    public static SaldoEntity fromDomain(Saldo saldo) { ... }
}
```

**🎯 Ventaja HEXAGONAL**: 
- Dominio puro sin dependencias de frameworks
- Inmutabilidad garantizada
- Lógica de negocio en el lugar correcto

---

### 2️⃣ SERVICIO / CASO DE USO

#### CAPAS:
```java
// ❌ SaldoService.java - Depende de implementaciones concretas
@Service
public class SaldoService {
    private final SaldoRepository saldoRepository;        // ⚠️ Spring Data
    private final SaldoCacheService cacheService;         // ⚠️ Servicio concreto
    private final CircuitBreakerRegistry circuitBreaker;  // ⚠️ Resilience4j
    private final MeterRegistry meterRegistry;            // ⚠️ Micrometer
    
    public Mono<SaldoResponse> consultarSaldo(String cuenta) {
        // ⚠️ Lógica de negocio mezclada con infraestructura
        return cacheService.obtener(cuenta)
                .switchIfEmpty(
                    saldoRepository.findByNumeroCuenta(cuenta)
                        .map(this::mapToResponse)  // ⚠️ Conversión manual
                );
    }
}
```

#### HEXAGONAL:
```java
// ✅ ConsultarSaldoUseCaseImpl.java - Depende de abstracciones
@Service
public class ConsultarSaldoUseCaseImpl implements ConsultarSaldoUseCase {
    private final SaldoRepositoryPort saldoRepository;  // ✅ Puerto (interfaz)
    private final SaldoCachePort cachePort;             // ✅ Puerto (interfaz)
    private final CircuitBreakerRegistry circuitBreaker;
    private final MeterRegistry meterRegistry;
    
    @Override
    public Mono<Saldo> consultarSaldo(String cuenta) {
        // ✅ Trabaja con modelos de dominio
        return cachePort.obtener(cuenta)
                .switchIfEmpty(
                    saldoRepository.findByNumeroCuenta(cuenta)
                        // ✅ Ya es Saldo (dominio), no necesita conversión
                );
    }
}
```

**🎯 Ventaja HEXAGONAL**: 
- Depende de abstracciones (puertos), no implementaciones
- Fácil de testear con mocks
- Trabaja directamente con modelos de dominio

---

### 3️⃣ REPOSITORIO

#### CAPAS:
```java
// ❌ SaldoRepository.java - Interfaz directa de Spring Data
public interface SaldoRepository 
        extends ReactiveCrudRepository<SaldoEntity, Long> {
    Mono<SaldoEntity> findByNumeroCuenta(String numeroCuenta);
}

// ⚠️ El servicio depende directamente de Spring Data
```

#### HEXAGONAL:
```java
// ✅ SaldoRepositoryPort.java - Puerto definido por el dominio
public interface SaldoRepositoryPort {
    Mono<Saldo> findByNumeroCuenta(String cuenta);  // ✅ Devuelve Saldo (dominio)
    Mono<Saldo> save(Saldo saldo);
    Mono<Void> deleteByNumeroCuenta(String cuenta);
}

// ✅ SaldoR2dbcRepository.java - Spring Data (interno del adaptador)
interface SaldoR2dbcRepository 
        extends ReactiveCrudRepository<SaldoEntity, Long> {
    Mono<SaldoEntity> findByNumeroCuenta(String cuenta);
}

// ✅ SaldoRepositoryAdapter.java - Implementa el puerto
@Component
public class SaldoRepositoryAdapter implements SaldoRepositoryPort {
    private final SaldoR2dbcRepository r2dbcRepository;
    
    @Override
    public Mono<Saldo> findByNumeroCuenta(String cuenta) {
        return r2dbcRepository.findByNumeroCuenta(cuenta)
                .map(SaldoEntity::toDomain);  // ✅ Entity → Domain
    }
}
```

**🎯 Ventaja HEXAGONAL**: 
- El dominio define QUÉ necesita (puerto)
- La infraestructura define CÓMO lo hace (adaptador)
- Fácil cambiar de R2DBC a MongoDB sin tocar el dominio

---

### 4️⃣ CACHE

#### CAPAS:
```java
// ❌ SaldoCacheService.java - Servicio concreto
@Service
public class SaldoCacheService {
    private final ReactiveRedisTemplate<String, SaldoResponse> redis;
    
    public Mono<SaldoResponse> obtener(String cuenta) {
        return redis.opsForValue().get(cuenta);
    }
}

// ⚠️ El servicio depende directamente de Redis
// ⚠️ Difícil cambiar a Hazelcast o Caffeine
```

#### HEXAGONAL:
```java
// ✅ SaldoCachePort.java - Puerto definido por el dominio
public interface SaldoCachePort {
    Mono<Saldo> obtener(String cuenta);        // ✅ Devuelve Saldo (dominio)
    Mono<Boolean> guardar(String cuenta, Saldo saldo);
    Mono<Boolean> invalidar(String cuenta);
}

// ✅ RedisCacheAdapter.java - Implementa el puerto
@Component
public class RedisCacheAdapter implements SaldoCachePort {
    private final ReactiveRedisTemplate<String, Saldo> redis;
    
    @Override
    public Mono<Saldo> obtener(String cuenta) {
        return redis.opsForValue().get(cuenta)
                .onErrorResume(error -> Mono.empty());  // ✅ Resiliente
    }
}
```

**🎯 Ventaja HEXAGONAL**: 
- Cambiar a Hazelcast solo requiere crear `HazelcastCacheAdapter`
- El dominio no sabe si es Redis, Hazelcast o Caffeine
- Más resiliente con manejo de errores

---

### 5️⃣ CONTROLLER

#### CAPAS:
```java
// ❌ SaldoController.java
@RestController
@RequestMapping("/saldos")
public class SaldoController {
    private final SaldoService saldoService;  // ⚠️ Depende del servicio concreto
    
    @GetMapping("/{cuenta}")
    public Mono<SaldoResponse> obtenerSaldo(@PathVariable String cuenta) {
        return saldoService.consultarSaldo(cuenta);
    }
}
```

#### HEXAGONAL:
```java
// ✅ SaldoController.java
@RestController
@RequestMapping("/api/v1/saldos")
public class SaldoController {
    private final ConsultarSaldoUseCase consultarSaldo;  // ✅ Depende de la interfaz
    
    @GetMapping("/{cuenta}")
    public Mono<SaldoResponse> obtenerSaldo(@PathVariable String cuenta) {
        return consultarSaldo.consultarSaldo(cuenta)
                .map(saldo -> new SaldoResponse(
                    saldo.getNumeroCuenta(), 
                    saldo.getMonto()
                ));
    }
}
```

**🎯 Ventaja HEXAGONAL**: 
- Controller depende del caso de uso (abstracción)
- Conversión explícita: Dominio → DTO
- Más fácil versionar API (/api/v1, /api/v2)

---

## 📈 Resumen de Ventajas

| Característica | CAPAS | HEXAGONAL |
|---------------|-------|-----------|
| **Acoplamiento** | Alto ⚠️ | Bajo ✅ |
| **Testabilidad** | Difícil ⚠️ | Fácil ✅ |
| **Cambiar tecnología** | Difícil ⚠️ | Fácil ✅ |
| **Dominio puro** | No ❌ | Sí ✅ |
| **SOLID** | Parcial ⚠️ | Total ✅ |
| **Curva de aprendizaje** | Baja ✅ | Media ⚠️ |
| **Líneas de código** | Menos ✅ | Más ⚠️ |

---

## 🧪 Testing Comparison

### CAPAS:
```java
// ⚠️ Test de integración (requiere infraestructura)
@SpringBootTest
@Testcontainers
class SaldoServiceTest {
    @Container
    static PostgreSQLContainer<?> postgres = ...;
    
    @Container
    static GenericContainer<?> redis = ...;
    
    @Autowired
    private SaldoService service;
    
    @Test
    void testConsultarSaldo() {
        // ⚠️ Test lento (arranca BD, Redis)
        // ⚠️ Test frágil (depende de infraestructura)
    }
}
```

### HEXAGONAL:
```java
// ✅ Test unitario (sin infraestructura)
class ConsultarSaldoUseCaseTest {
    private SaldoRepositoryPort mockRepo;
    private SaldoCachePort mockCache;
    private ConsultarSaldoUseCase useCase;
    
    @BeforeEach
    void setup() {
        mockRepo = mock(SaldoRepositoryPort.class);
        mockCache = mock(SaldoCachePort.class);
        useCase = new ConsultarSaldoUseCaseImpl(mockRepo, mockCache, ...);
    }
    
    @Test
    void testConsultarSaldo() {
        // ✅ Test rápido (sin BD ni Redis)
        // ✅ Test robusto (mocks)
        when(mockCache.obtener("123")).thenReturn(Mono.empty());
        when(mockRepo.findByNumeroCuenta("123"))
            .thenReturn(Mono.just(new Saldo("123", 1000.0)));
        
        StepVerifier.create(useCase.consultarSaldo("123"))
            .expectNext(new Saldo("123", 1000.0))
            .verifyComplete();
    }
}
```

---

## 🎯 ¿Cuándo usar cada una?

### Usar CAPAS cuando:
- ✅ Proyecto pequeño / POC
- ✅ Equipo junior / aprendiendo
- ✅ Time-to-market es crítico
- ✅ No se esperan cambios de tecnología

### Usar HEXAGONAL cuando:
- ✅ Proyecto mediano/grande
- ✅ Equipo experimentado
- ✅ Alta testabilidad requerida
- ✅ Posibles cambios de tecnología
- ✅ Múltiples adaptadores (REST, gRPC, Kafka)
- ✅ Dominio complejo

---

## 🔄 Migración CAPAS → HEXAGONAL

### Pasos sugeridos:
1. ✅ Crear estructura de carpetas hexagonal
2. ✅ Crear modelo de dominio puro (Saldo)
3. ✅ Crear puertos (interfaces)
4. ✅ Crear casos de uso (implementaciones)
5. ✅ Crear adaptadores (persistence, cache)
6. ✅ Migrar controllers
7. ✅ Tests unitarios
8. ⏭️ Deprecar código antiguo
9. ⏭️ Eliminar código antiguo

---

**📌 Nota**: En este proyecto, ambas arquitecturas coexisten para aprendizaje.
En producción, elegirías UNA arquitectura según tus necesidades.
