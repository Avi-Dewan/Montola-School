# Montola School — Backend Index

> Quick reference for navigating the codebase. See also: [ARCHITECTURE.md](./ARCHITECTURE.md) | [CONVENTIONS.md](./CONVENTIONS.md)
>
> **Frontend context →** `Montola-School-FrontEnd/.context/`

---

## Entry Points

| What | File |
|------|------|
| Application startup | `src/main/java/com/montola/school/MontolaApplication.java` |
| Main config | `src/main/resources/application.yml` |
| Dev config | `src/main/resources/application-dev.yml` |
| Prod config | `src/main/resources/application-prod.yml` |
| Security filter chain | `common/config/SecurityConfig.java` |
| JWT filter | `common/security/JwtAuthenticationFilter.java` |
| Global error handler | `common/exception/GlobalExceptionHandler.java` |
| Base entity | `common/model/Persistent.java` |

---

## Run Scripts

| Script | Purpose |
|--------|---------|
| `dev-db.sh` | Start PostgreSQL Docker container |
| `dev-run.sh` | Build + run (dev profile, local DB) |
| `docker-run.sh` | Build Docker image + run container |
| `prod-run-local.sh` | Run with prod profile (Neon DB) |

---

## Package Index

### `admin/` — Admin Dashboard

| File | Purpose |
|------|---------|
| `controller/AdminController.java` | `GET /api/v1/admin/statistics` |
| `dto/AdminStatisticsDto.java` | Dashboard stats response |
| `service/AdminService.java` | Stats aggregation interface |
| `service/impl/AdminServiceImpl.java` | Stats implementation |

### `auth/` — Authentication & Users

| File | Purpose |
|------|---------|
| **Controllers** | |
| `controller/AuthController.java` | Login, register, activate, password reset, token refresh |
| `controller/UserController.java` | User CRUD, profile pictures |
| **DTOs** | |
| `dto/LoginRequest.java` | Login input |
| `dto/UserRegisterRequest.java` | Student registration input |
| `dto/AdminRegistrationRequest.java` | Admin-created user input |
| `dto/AuthResponse.java` | Login response (tokens + user) |
| `dto/UserResponse.java` | User output |
| `dto/ActivationRequest.java` | Email activation input |
| `dto/RefreshTokenRequest.java` | Token refresh input |
| `dto/RefreshTokenResponse.java` | Token refresh output |
| `dto/ChangePasswordRequest.java` | Password change input |
| `dto/EmailRequest.java` | Forgot password input |
| `dto/ResetPasswordRequest.java` | Password reset input |
| **Enums** | |
| `enums/Role.java` | `ADMIN, MANAGER, TEACHER, STUDENT` |
| **Models** | |
| `model/User.java` | User entity (email, password, roles, profile photo) |
| `model/ActivationToken.java` | Email verification token |
| `model/RefreshToken.java` | JWT refresh token |
| `model/ResetPasswordToken.java` | Password reset token |
| **Security** | |
| `security/CustomUserDetails.java` | Spring Security UserDetails adapter |
| `security/JpaUserDetailsService.java` | UserDetailsService implementation |
| **Services** | |
| `service/AuthService.java` | Auth business logic interface |
| `service/impl/AuthServiceImpl.java` | Auth implementation (login, register, activate) |
| `service/UserService.java` | User management interface |
| `service/impl/UserServiceImpl.java` | User management implementation |
| `service/ActivationTokenService.java` | Token generation/validation |
| `service/RefreshTokenService.java` | Refresh token interface |
| `service/impl/RefreshTokenServiceImpl.java` | Refresh token implementation |
| `service/ResetPasswordTokenService.java` | Reset token management |
| **Mapper** | |
| `mapper/UserMapper.java` | User ↔ UserResponse mapping |

### `common/` — Shared Infrastructure

| File | Purpose |
|------|---------|
| **Config** | |
| `config/SecurityConfig.java` | CORS, CSRF, filter chain, public endpoints |
| `config/OpenApiConfig.java` | Swagger UI + JWT auth scheme |
| `config/ResendConfig.java` | Resend email client bean |
| `config/MessageConfig.java` | i18n message source |
| **Security** | |
| `security/JwtAuthenticationFilter.java` | JWT extraction + validation filter |
| `security/JwtService.java` | JWT token generation + parsing |
| `security/JwtProperties.java` | JWT config properties (secret, expiry) |
| `security/SecurityProperties.java` | Whitelisted public paths |
| `security/CustomAuthenticationEntryPoint.java` | 401 error handler |
| `security/CustomAccessDeniedHandler.java` | 403 error handler |
| **Exception** | |
| `exception/GlobalExceptionHandler.java` | Centralized error handling |
| `exception/ResourceNotFoundException.java` | 404 |
| `exception/ResourceAlreadyExistsException.java` | 409 |
| `exception/TokenExpiredException.java` | Token expired |
| `exception/TokenMissingException.java` | No token provided |
| `exception/RegistrationTokenExpiredException.java` | Activation token expired |
| `exception/UserNotActivatedException.java` | Unverified user login |
| `exception/ChapterNotAuthorizedException.java` | Teacher not assigned |
| `exception/InvalidContentTypeException.java` | Wrong content type |
| `exception/InvalidQuizTypeException.java` | Wrong quiz type |
| `exception/ContentAccessDeniedException.java` | Content not purchased |
| `exception/SequentialAccessException.java` | Out-of-order content access |
| **Model** | |
| `model/Persistent.java` | Base entity (version, timestamps, soft delete) |
| **DTO** | |
| `dto/ErrorResponse.java` | Standardized error response |
| **Service** | |
| `service/BusinessEmailService.java` | Email sending (activation, reset, purchase) |

### `course/` — Course Content Management

| File | Purpose |
|------|---------|
| **Controllers** | |
| `controller/ClassController.java` | `/api/v1/classes` CRUD + structure |
| `controller/SubjectController.java` | `/api/v1/subjects` CRUD + structure |
| `controller/ChapterController.java` | `/api/v1/chapters` CRUD + assign teachers + status + images |
| `controller/TopicController.java` | `/api/v1/topics` CRUD |
| `controller/ContentController.java` | `/api/v1/contents` CRUD (lecture/quiz/PDF) |
| `controller/FeaturedChapterController.java` | `/api/v1/featured-chapters` management |
| `controller/TeacherController.java` | `/api/v1/teachers` assigned chapters + statistics |
| **Enums** | |
| `enums/ChapterStatus.java` | `DRAFT, PUBLISHED, ARCHIVED` |
| `enums/ContentType.java` | `LECTURE, QUIZ, PDF, ASSIGNMENT` |
| `enums/QuizType.java` | `MCQ, WRITTEN, FILL_BLANK, TABLE_MATCHING` |
| `enums/QuestionType.java` | Per-question type |
| `enums/StorageProvider.java` | File storage provider |
| **Models** | |
| `model/ClassEntity.java` | Class (name, description) → has Subjects |
| `model/Subject.java` | Subject → has Chapters |
| `model/Chapter.java` | Chapter (title, price, status, cover) → has Topics |
| `model/Topic.java` | Topic → has ContentItems |
| `model/ContentItem.java` | Content wrapper (type, order) → has Lecture/Quiz/PDF |
| `model/FeaturedChapter.java` | Featured chapter (OneToOne with Chapter) |
| `model/ChapterTeacher.java` | Teacher-Chapter assignment join |
| `model/contents/Lecture.java` | Video + text content |
| `model/contents/Quiz.java` | Quiz with questions |
| `model/contents/file/GooglePdfContent.java` | Google Drive PDF reference |
| `model/contents/quiz/QuizQuestion.java` | Question (text, type, marks) |
| `model/contents/quiz/QuizOption.java` | MCQ option |
| `model/contents/quiz/QuizFillBlank.java` | Fill-in-the-blank answer |
| `model/contents/quiz/QuizTableMatching.java` | Matching pair |
| `model/contents/quiz/QuizWrittenAnswer.java` | Sample written answer |
| **Services** | |
| `service/ChapterAuthorizationService.java` | Teacher ↔ chapter access check |
| Plus 11 more service interfaces + implementations |

### `learner/` — Enrollment & Progress

| File | Purpose |
|------|---------|
| `controller/ProgressController.java` | `/api/v1/progress` endpoints |
| `model/Enrollment.java` | User ↔ Chapter enrollment |
| `model/ContentProgress.java` | Per-content completion tracking |
| `service/ProgressService.java` | Progress interface |
| `service/ProgressServiceImpl.java` | Progress implementation |

### `payment/` — Payment Processing

| File | Purpose |
|------|---------|
| `controller/PaymentController.java` | `/api/v1/payments` submit, verify, reject |
| `controller/FreeEnrollmentController.java` | `/api/v1/enrollments/free` |
| `enums/PaymentStatus.java` | `PENDING, VERIFIED, REJECTED` |
| `model/Payment.java` | Payment entity |
| `service/PaymentService.java` | Payment interface |
| `service/impl/PaymentServiceImpl.java` | Payment implementation |
| `service/FreeEnrollmentService.java` | Free enrollment interface |
| `service/impl/FreeEnrollmentServiceImpl.java` | Free enrollment implementation |

---

## Resource Files

| File | Purpose |
|------|---------|
| `application.yml` | Main config (port, JPA, JWT, Resend, Actuator) |
| `application-dev.yml` | Dev profile (local DB, flyway off) |
| `application-prod.yml` | Prod profile (Neon DB, flyway on) |
| `messages.properties` | Error message keys for i18n |
| `db/migration/init/V1-V7` | Core schema migrations |
| `db/migration/2026.1.1/V8-V11` | Feature additions |
| `db/migration/2026.3.1/V12` | Schema refinements |

---

## Infrastructure Files

| File | Purpose |
|------|---------|
| `build.gradle` | Dependencies, plugins, Java 21 toolchain |
| `settings.gradle` | Root project name |
| `Dockerfile` | Multi-stage build (JDK 21 → JRE 21 Alpine) |
| `dev-setup/docker-compose.yml` | PostgreSQL 16 container |
| `.gitignore` | Ignores build/, .gradle/, .env*, .idea/ |
