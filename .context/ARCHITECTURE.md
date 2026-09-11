# Montola School — Backend Architecture

> This document provides architectural context for AI coding assistants and developers onboarding to the project.

---

## System Overview

Montola School is a 2-tier web application with a separate frontend and backend:

```mermaid
graph LR
    subgraph Client
        FE["Next.js Frontend<br/>:3000"]
    end

    subgraph Server
        API["Spring Boot API<br/>:8080"]
    end

    subgraph Data
        DB[("PostgreSQL 16<br/>(Docker / Neon)")]
    end

    subgraph External
        RESEND["Resend Email API"]
    end

    FE -- "REST API (JSON)<br/>JWT Auth" --> API
    API -- "JPA / Hibernate" --> DB
    API -- "Transactional Email" --> RESEND
```

- **Frontend** → Next.js 16 at `:3000` — consumes REST API
- **Backend** → Spring Boot 3.5.5 at `:8080` — serves JSON API
- **Database** → PostgreSQL 16 — local Docker (dev) or Neon cloud (prod)
- **Email** → Resend API — activation, password reset, purchase confirmation

---

## Layered Architecture

The backend follows a **layered architecture** within each domain package:

```mermaid
graph TD
    HTTP["HTTP Request"] --> FILTER["JwtAuthenticationFilter"]
    FILTER --> CONTROLLER["Controller<br/>(REST endpoints)"]
    CONTROLLER --> DTO_IN["Request DTO<br/>(validated)"]
    DTO_IN --> SERVICE["Service Interface"]
    SERVICE --> IMPL["ServiceImpl<br/>(business logic)"]
    IMPL --> REPO["JPA Repository"]
    REPO --> DB[("PostgreSQL")]
    IMPL --> MAPPER["MapStruct Mapper"]
    MAPPER --> DTO_OUT["Response DTO"]
    DTO_OUT --> CONTROLLER
    IMPL -.-> EMAIL["BusinessEmailService"]
    EMAIL -.-> RESEND["Resend API"]

    style FILTER fill:#f9e2af
    style CONTROLLER fill:#89b4fa
    style SERVICE fill:#a6e3a1
    style IMPL fill:#a6e3a1
    style REPO fill:#cba6f7
    style MAPPER fill:#fab387
```

**Flow**: HTTP → JWT Filter → Controller → Service (interface) → ServiceImpl → Repository → Database

Each layer has a clear responsibility:

| Layer | Responsibility | Location |
|-------|---------------|----------|
| **Filter** | JWT authentication, whitelist bypass | `common/security/JwtAuthenticationFilter` |
| **Controller** | HTTP endpoint mapping, request validation, authorization | `*/controller/` |
| **DTO** | Data transfer between layers, request/response shaping | `*/dto/` |
| **Service** | Business logic interface | `*/service/*.java` (interface) |
| **ServiceImpl** | Business logic implementation | `*/service/impl/*.java` |
| **Repository** | Data access (Spring Data JPA) | `*/repository/` |
| **Mapper** | Entity ↔ DTO conversion (MapStruct) | `*/mapper/` |
| **Model** | JPA entities | `*/model/` |

---

## Domain Package Structure

The codebase uses **domain-driven packaging** — code is organized by business domain, not by technical layer:

```mermaid
graph TD
    ROOT["com.montola.school"] --> ADMIN["admin/"]
    ROOT --> AUTH["auth/"]
    ROOT --> COMMON["common/"]
    ROOT --> COURSE["course/"]
    ROOT --> LEARNER["learner/"]
    ROOT --> PAYMENT["payment/"]

    ADMIN --> A1["controller/"]
    ADMIN --> A2["dto/"]
    ADMIN --> A3["service/"]

    AUTH --> B1["controller/ — AuthController, UserController"]
    AUTH --> B2["dto/ — 11 DTOs"]
    AUTH --> B3["enums/ — Role"]
    AUTH --> B4["model/ — User, tokens"]
    AUTH --> B5["repository/"]
    AUTH --> B6["security/ — UserDetails"]
    AUTH --> B7["service/"]

    COMMON --> C1["config/ — Security, OpenAPI, Resend"]
    COMMON --> C2["exception/ — 11 exceptions + GlobalHandler"]
    COMMON --> C3["model/ — Persistent base entity"]
    COMMON --> C4["security/ — JWT filter, service, properties"]

    COURSE --> D1["controller/ — 7 controllers"]
    COURSE --> D2["dto/ — 36 DTOs"]
    COURSE --> D3["model/ — 7 entities + content subtypes"]
    COURSE --> D4["repository/ — 15 repositories"]
    COURSE --> D5["service/ — 12 services"]

    LEARNER --> E1["controller/ — ProgressController"]
    LEARNER --> E2["model/ — Enrollment, ContentProgress"]

    PAYMENT --> F1["controller/ — Payment, FreeEnrollment"]
    PAYMENT --> F2["model/ — Payment"]
```

| Package | Responsibility |
|---------|---------------|
| `admin` | Admin dashboard statistics |
| `auth` | User registration, login, JWT tokens, password management, profile pictures |
| `common` | Shared infrastructure — security config, exception handling, base entity, email service |
| `course` | Course content management — classes, subjects, chapters, topics, content items (lectures/quizzes/PDFs), teacher assignments, featured chapters |
| `learner` | Student enrollment and progress tracking |
| `payment` | Payment submission, verification, and free enrollment |
| `shop` | Shop catalog (products, bundles), entitlements, shop payments, admin management |
| `care` | Academic Care enquiry lead capture and follow-up |
| `notice` | Homepage notices |

---

## Authentication & Authorization Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant F as JwtFilter
    participant S as SecurityContext
    participant CT as Controller
    participant A as AuthService

    Note over C,A: Registration & Activation
    C->>CT: POST /api/auth/register
    CT->>A: createUser(dto)
    A->>A: hash password (BCrypt)
    A->>A: save User (inactive)
    A->>A: generate ActivationToken
    A->>C: 201 Created + activation email sent

    C->>CT: POST /api/auth/activate {email, token}
    CT->>A: activate(email, token)
    A->>A: verify token, mark user active

    Note over C,A: Login Flow
    C->>CT: POST /api/auth/login {email, password}
    CT->>A: authenticate(email, password)
    A->>A: verify BCrypt hash
    A->>A: generate JWT (60 min)
    A->>A: generate RefreshToken (DB)
    A-->>C: {accessToken, refreshToken, user}

    Note over C,A: Authenticated Request
    C->>F: GET /api/v1/chapters (Bearer token)
    F->>F: extract & validate JWT
    F->>S: set SecurityContext
    S->>CT: authorized request
    CT-->>C: 200 OK + data

    Note over C,A: Token Refresh
    C->>CT: POST /api/auth/refresh-token {refreshToken}
    CT->>A: refresh(token)
    A->>A: validate RefreshToken in DB
    A->>A: generate new JWT
    A-->>C: {accessToken}
```

### Authorization Layers

1. **URL-level whitelisting** — Public endpoints bypass JWT filter entirely (`SecurityProperties.whitelist`)
2. **JWT filter** — Validates token, extracts user, sets `SecurityContext`
3. **Method-level** — `@PreAuthorize("hasRole('ADMIN')")` on controller methods
4. **Business-level** — `ChapterAuthorizationService` checks teacher-chapter assignment

### Role Hierarchy

```
ADMIN ─── Full access to everything
MANAGER ── Same as Admin (shares admin dashboard)
TEACHER ── Edit assigned chapters, topics, content only
STUDENT ── View purchased/free content, track progress
```

Users can hold **multiple roles** simultaneously.

---

## Entity Relationship Diagram

```mermaid
erDiagram
    USER {
        Long id PK
        String email UK
        String fullName
        String passwordHash
        String phone
        byte[] profilePhoto
        boolean isActivated
    }

    USER ||--o{ ACTIVATION_TOKEN : "has"
    USER ||--o{ REFRESH_TOKEN : "has"
    USER ||--o{ RESET_PASSWORD_TOKEN : "has"
    USER ||--o{ ENROLLMENT : "enrolls in"
    USER ||--o{ PAYMENT : "makes"
    USER ||--o{ CONTENT_PROGRESS : "tracks"

    CLASS_ENTITY {
        Long id PK
        String name
        String description
    }

    CLASS_ENTITY ||--o{ SUBJECT : "contains"

    SUBJECT {
        Long id PK
        int orderIndex
        String name
        String description
    }

    SUBJECT ||--o{ CHAPTER : "contains"

    CHAPTER {
        Long id PK
        int orderIndex
        String title
        String description
        byte[] coverImage
        String status
        String videoId
        Long price
        boolean isFree
    }

    CHAPTER ||--o{ TOPIC : "contains"
    CHAPTER ||--o{ CHAPTER_TEACHER : "assigned to"
    CHAPTER ||--o| FEATURED_CHAPTER : "featured as"
    CHAPTER ||--o{ ENROLLMENT : "enrolled via"
    CHAPTER ||--o{ PAYMENT : "paid for"

    CHAPTER_TEACHER {
        Long id PK
        Timestamp assignedAt
    }

    CHAPTER_TEACHER }o--|| USER : "teacher"

    TOPIC {
        Long id PK
        int orderIndex
        String title
        String description
    }

    TOPIC ||--o{ CONTENT_ITEM : "contains"

    CONTENT_ITEM {
        Long id PK
        String title
        String type
        int orderIndex
    }

    CONTENT_ITEM ||--o| LECTURE : "is a"
    CONTENT_ITEM ||--o| QUIZ : "is a"
    CONTENT_ITEM ||--o| GOOGLE_PDF_CONTENT : "is a"
    CONTENT_ITEM ||--o{ CONTENT_PROGRESS : "tracked by"

    LECTURE {
        Long id PK
        String videoId
        String content
    }

    QUIZ {
        Long id PK
        String quizType
        String instruction
        int timeLimit
        int totalMarks
        int passPercentage
    }

    QUIZ ||--o{ QUIZ_QUESTION : "has"

    QUIZ_QUESTION {
        Long id PK
        String questionText
        String type
        int orderIndex
        int marks
    }

    QUIZ_QUESTION ||--o{ QUIZ_OPTION : "has options"
    QUIZ_QUESTION ||--o{ QUIZ_FILL_BLANK : "has blanks"
    QUIZ_QUESTION ||--o{ QUIZ_TABLE_MATCHING : "has matches"
    QUIZ_QUESTION ||--o| QUIZ_WRITTEN_ANSWER : "has answer"

    ENROLLMENT {
        Long id PK
        Timestamp enrolledAt
        float progressPercentage
        boolean isCompleted
    }

    PAYMENT {
        Long id PK
        String senderNumber
        String transactionId
        Long amount
        String paymentMethod
        String status
    }

    CONTENT_PROGRESS {
        Long id PK
        boolean isCompleted
        Float quizScore
        Timestamp lastAccessed
    }
```

---

## Course Content Hierarchy

```
Class (e.g., "Class 6")
 └── Subject (e.g., "Mathematics")
      └── Chapter (e.g., "Algebra Basics") ← purchasable unit
           ├── Cover Image
           ├── Price / Free flag
           ├── Status: DRAFT → PUBLISHED → ARCHIVED
           ├── Assigned Teachers (ChapterTeacher join)
           └── Topic (e.g., "Variables and Constants")
                └── ContentItem (ordered within topic)
                     ├── LECTURE — video ID + rich text content
                     ├── QUIZ — MCQ, fill-blank, matching, written
                     └── PDF — Google Drive file reference
```

**Key points:**
- **Chapter** is the purchasable unit (not class or subject)
- Content is accessed **sequentially** — students must complete previous content first
- Teachers are **assigned** to specific chapters and can only edit their assigned chapters
- Chapters can be **free** (no payment required) or **paid**

---

## Payment Flow

```mermaid
stateDiagram-v2
    [*] --> StudentBrowses: Student finds a chapter
    StudentBrowses --> FreeEnroll: Chapter is free
    StudentBrowses --> SubmitPayment: Chapter is paid

    FreeEnroll --> Enrolled: POST /enrollments/free/{id}

    SubmitPayment --> PENDING: POST /payments/submit<br/>(bKash/Nagad details)

    PENDING --> VERIFIED: Admin verifies<br/>PUT /payments/{id}/verify
    PENDING --> REJECTED: Admin rejects<br/>PUT /payments/{id}/reject

    VERIFIED --> Enrolled: Auto-enrollment created
    REJECTED --> StudentBrowses: Student can retry

    Enrolled --> [*]: Student accesses content
```

Payment methods: **bKash** and **Nagad** (Bangladeshi mobile payment services). Verification is **manual** by admin/manager.

---

## Database Strategy

| Aspect | Dev | Prod |
|--------|-----|------|
| Provider | Docker PostgreSQL 16 | Neon (cloud PostgreSQL) |
| Connection | `localhost:5432/montola` | `DB_URL` with SSL |
| Schema Management | Hibernate `validate` | Flyway migrations |
| Flyway | Enabled (baselined at V12 for pre-existing DBs) | Enabled |
| Pool | Default | HikariCP (max 5) |

### Flyway Migrations

Organized in versioned folders:

```
db/migration/
├── init/                  # V1–V7: Core schema
│   ├── V1__users.sql
│   ├── V2__activation_tokens_and_reset_password.sql
│   ├── V3__courses.sql
│   ├── V4__contents.sql
│   ├── V5__chapter_teacher.sql
│   ├── V6__payments.sql
│   └── V7__content_progress.sql
├── 2026.1.1/              # V8–V11: Feature additions
│   ├── V8__update_enrollment_and_user.sql
│   ├── V9__add_refresh_token_table.sql
│   ├── V10__add_chapter_cover_image.sql
│   └── V11__featured_chapters.sql
└── 2026.3.1/              # V12: Schema refinement
    └── V12__add_primary_keys.sql
```

The shop/care/notice modules add a fourth folder:

```
db/migration/2026.4.1/
├── V13__levels.sql                          # Levels (JSC/SSC/HSC) + classes.level_id
├── V14__shop_products.sql                   # Shop products
├── V15__shop_bundles.sql                    # Bundles + bundle_products join
├── V16__shop_entitlements_and_payments.sql  # Entitlements + shop payments
├── V17__care_leads.sql                      # Academic Care leads
└── V18__notices.sql                         # Notices
```

---

## Base Entity (`Persistent`)

All domain entities extend `Persistent` (MappedSuperclass), which provides:

| Field | Type | Purpose |
|-------|------|---------|
| `version` | `@Version Long` | Optimistic locking |
| `createdAt` | `LocalDateTime` | Auto-set on creation |
| `updatedAt` | `LocalDateTime` | Auto-set on update |
| `isDeleted` | `boolean` | Soft delete flag (default: `false`) |

---

## Exception Handling

All exceptions flow through `GlobalExceptionHandler` which returns standardized `ErrorResponse`:

```json
{
  "status": 404,
  "message": "Chapter not found",
  "timestamp": "2026-05-29T14:30:00",
  "fieldErrors": null
}
```

Custom exceptions: `ResourceNotFoundException`, `ResourceAlreadyExistsException`, `TokenExpiredException`, `TokenMissingException`, `RegistrationTokenExpiredException`, `UserNotActivatedException`, `ChapterNotAuthorizedException`, `InvalidContentTypeException`, `InvalidQuizTypeException`, `ContentAccessDeniedException`, `SequentialAccessException`.

---

## External Integrations

| Service | Library | Purpose |
|---------|---------|---------|
| **Resend** | `resend-java:2.0.0` | Transactional emails (activation, password reset, purchase confirmation) |
| **Google Drive** | URL reference only | PDF content storage (fileId stored, rendered via Google Docs viewer) |
| **AWS S3** | `software.amazon.awssdk:s3` | Shop product files. Private bucket, short-lived presigned URLs, behind `FileStorageService`. Optional — the default `external` provider keeps a supplied reference instead. |

Emails are sent from `Montola School <noreply@montolaschool.com>` with HTML templates embedded in `BusinessEmailService`.
