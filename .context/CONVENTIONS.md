# Montola School — Backend Conventions

> Coding patterns, naming conventions, and how-to guides for contributors and AI assistants.

---

## Package Organization

Code is organized by **business domain**, not by technical layer:

```
com.montola.school/
├── admin/        # Admin dashboard
├── auth/         # Authentication & users
├── common/       # Shared infrastructure
├── course/       # Course content (classes, subjects, chapters, topics, content)
├── learner/      # Enrollment & progress
└── payment/      # Payments & free enrollment
```

Each domain package follows the same internal structure:

```
domain/
├── controller/   # REST endpoints (@RestController)
├── dto/          # Request/Response DTOs (@Valid, records or classes)
├── enums/        # Domain enumerations
├── mapper/       # MapStruct interfaces
├── model/        # JPA entities (@Entity)
├── repository/   # Spring Data JPA interfaces
└── service/      # Service interface + impl/ subdirectory
```

---

## Controller Pattern

Controllers handle HTTP concerns only — no business logic:

```java
@RestController
@RequestMapping("/api/v1/chapters")
@RequiredArgsConstructor
@Tag(name = "Chapter Management")
public class ChapterController {

    private final ChapterService chapterService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new chapter")
    public ResponseEntity<ChapterResponse> create(@Valid @RequestBody CreateChapterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chapterService.create(request));
    }
}
```

**Key conventions:**
- `@RequiredArgsConstructor` for constructor injection (via Lombok)
- `@PreAuthorize` for role-based access control
- `@Valid` on request DTOs for input validation
- `@Tag` and `@Operation` for OpenAPI documentation
- Return `ResponseEntity<T>` with explicit status codes
- Delegate all logic to the service layer

---

## Service Pattern

Services use **interface + implementation** separation:

```java
// Service interface
public interface ChapterService {
    ChapterResponse create(CreateChapterRequest request);
    ChapterResponse getById(Long id);
    List<ChapterResponse> getAll();
}

// Implementation
@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;
    private final ChapterMapper chapterMapper;

    @Override
    public ChapterResponse create(CreateChapterRequest request) {
        Chapter chapter = chapterMapper.toEntity(request);
        chapter = chapterRepository.save(chapter);
        return chapterMapper.toResponse(chapter);
    }
}
```

**Key conventions:**
- Interface in `service/` directory
- Implementation in `service/impl/` subdirectory
- `@Service` annotation on implementations
- Constructor injection via `@RequiredArgsConstructor`
- Use MapStruct mappers for entity ↔ DTO conversion

---

## DTO Pattern

DTOs are typically Java records (immutable) with validation annotations:

```java
// Request DTO
public record CreateChapterRequest(
    @NotBlank String title,
    String description,
    @NotNull Long subjectId,
    Long price,
    boolean isFree
) {}

// Response DTO
public record ChapterResponse(
    Long id,
    String title,
    String description,
    String status,
    Long price,
    boolean isFree,
    LocalDateTime createdAt
) {}
```

**Conventions:**
- Request DTOs use `@NotBlank`, `@NotNull`, `@Valid` for validation
- Response DTOs are plain records (no validation)
- Field names match entity names where possible
- Nested responses use separate DTOs (e.g., `ChapterStructureDto`)

---

## MapStruct Mapping

Entity ↔ DTO conversions are handled by MapStruct:

```java
@Mapper(componentModel = "spring")
public interface ChapterMapper {
    ChapterResponse toResponse(Chapter chapter);
    Chapter toEntity(CreateChapterRequest request);
}
```

**Conventions:**
- `componentModel = "spring"` for Spring injection
- One mapper per domain entity
- Custom mappings via `@Mapping` when field names differ
- Mappers are in `*/mapper/` directories

---

## Entity Pattern

All entities extend `Persistent` for common fields:

```java
@Entity
@Table(name = "chapters")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Chapter extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chapter_seq")
    @SequenceGenerator(name = "chapter_seq", sequenceName = "chapters_id_seq", allocationSize = 1)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private ChapterStatus status = ChapterStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL)
    @OrderBy("orderIndex ASC")
    private List<Topic> topics = new ArrayList<>();
}
```

**Conventions:**
- Extend `Persistent` for audit fields + soft delete + optimistic locking
- Use `@SequenceGenerator` for PostgreSQL sequence-based IDs
- Use `FetchType.LAZY` for `@ManyToOne` relationships
- Use `@OrderBy` for ordered collections
- Lombok annotations: `@Getter @Setter @NoArgsConstructor @AllArgsConstructor`
- Enum fields use `@Enumerated(EnumType.STRING)`

---

## Exception Handling

### Creating Custom Exceptions

Custom exceptions are in `common/exception/`:

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

### Global Handler

`GlobalExceptionHandler` catches all exceptions and returns `ErrorResponse`:

```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(404, ex.getMessage(), LocalDateTime.now(), null));
}
```

**Conventions:**
- Use message keys from `messages.properties` for error messages
- Always return `ErrorResponse` with status, message, timestamp, and optional field errors
- Validation errors include `fieldErrors` map
- Log exceptions at appropriate levels (WARN for client errors, ERROR for server errors)

---

## Security Annotations

```java
// Admin/Manager only
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")

// Any authenticated user
@PreAuthorize("isAuthenticated()")

// Admin only
@PreAuthorize("hasRole('ADMIN')")

// Self or Admin/Manager
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #id == authentication.principal.id")
```

For **chapter-level authorization** (teachers can only edit assigned chapters):

```java
// In service implementations
chapterAuthorizationService.checkAuthorization(chapterId);
```

This checks if the current user is Admin/Manager OR a teacher assigned to the chapter.

---

## Naming Conventions

| Item | Convention | Example |
|------|-----------|---------|
| Package | lowercase, domain-based | `com.montola.school.course` |
| Entity class | PascalCase, singular noun | `Chapter`, `ContentItem` |
| Repository | `{Entity}Repository` | `ChapterRepository` |
| Service interface | `{Entity}Service` | `ChapterService` |
| Service impl | `{Entity}ServiceImpl` | `ChapterServiceImpl` |
| Controller | `{Entity}Controller` | `ChapterController` |
| Mapper | `{Entity}Mapper` | `ChapterMapper` |
| Request DTO | `Create{Entity}Request`, `Update{Entity}Request` | `CreateChapterRequest` |
| Response DTO | `{Entity}Response`, `{Entity}Dto` | `ChapterResponse` |
| Enum | PascalCase class, UPPER_SNAKE values | `ChapterStatus.PUBLISHED` |
| DB table | lowercase, plural, snake_case | `chapters`, `content_items` |
| DB column | lowercase, snake_case | `created_at`, `subject_id` |
| URL path | kebab-case, plural nouns | `/api/v1/featured-chapters` |

---

## How to Add a New Feature

### Example: Adding a "Notice Board" feature

#### 1. Create the domain package

```
src/main/java/com/montola/school/notice/
├── controller/
├── dto/
├── model/
├── repository/
└── service/
    └── impl/
```

#### 2. Create the entity

```java
@Entity
@Table(name = "notices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Notice extends Persistent {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notice_seq")
    @SequenceGenerator(name = "notice_seq", sequenceName = "notices_id_seq", allocationSize = 1)
    private Long id;

    private String title;
    private String content;
    private boolean isPublished;
}
```

#### 3. Create the repository

```java
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByIsPublishedTrue();
}
```

#### 4. Create DTOs

```java
public record CreateNoticeRequest(@NotBlank String title, @NotBlank String content) {}
public record NoticeResponse(Long id, String title, String content, boolean isPublished, LocalDateTime createdAt) {}
```

#### 5. Create the mapper

```java
@Mapper(componentModel = "spring")
public interface NoticeMapper {
    NoticeResponse toResponse(Notice notice);
}
```

#### 6. Create the service

```java
public interface NoticeService {
    NoticeResponse create(CreateNoticeRequest request);
    List<NoticeResponse> getPublished();
}

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    // implementation
}
```

#### 7. Create the controller

```java
@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
@Tag(name = "Notice Board")
public class NoticeController {
    // endpoints
}
```

#### 8. Create the Flyway migration

```sql
-- src/main/resources/db/migration/{version_folder}/V{N}__add_notices.sql
CREATE TABLE notices (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    is_published BOOLEAN DEFAULT false,
    version BIGINT DEFAULT 0,
    is_deleted BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE notices_id_seq OWNED BY notices.id;
```

#### 9. Add error messages

In `messages.properties`:
```properties
notice.notfound=Notice not found
```

---

## Configuration Reference

| File | Purpose |
|------|---------|
| `application.yml` | Main config (port, JPA, JWT, email, actuator) |
| `application-dev.yml` | Dev overrides (local DB, flyway off) |
| `application-prod.yml` | Prod overrides (Neon DB with SSL, flyway on) |
| `messages.properties` | i18n error message keys |
| `SecurityConfig.java` | CORS, CSRF, filter chain, role configuration |
| `JwtProperties.java` | JWT secret + expiration (from env) |
| `OpenApiConfig.java` | Swagger UI + Bearer auth scheme |
| `ResendConfig.java` | Resend email client setup |

---

## Testing Notes

- Test dependencies are configured (`spring-boot-starter-test`, `spring-security-test`, JUnit 5)
- No test files exist yet (`src/test/` directory is absent)
- Docker build skips tests (`-x test`)
- TODO: Add unit tests for services and integration tests for controllers
