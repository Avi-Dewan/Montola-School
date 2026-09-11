# 🏫 Montola School — Backend API

The backend API server for **Montola School**, an online learning management system serving students in the Chittagong Hill Tracts, Bangladesh. Built with Java 21 and Spring Boot 3.5.5.

> **Frontend repo →** [Montola-School-FrontEnd](https://github.com/Avi-Dewan/Montola-School-FrontEnd)

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.5.5 |
| **Database** | PostgreSQL 16 |
| **ORM** | Spring Data JPA / Hibernate |
| **Migrations** | Flyway |
| **Auth** | JWT (jjwt 0.11.5) + Spring Security |
| **Email** | Resend (resend-java 2.0.0) |
| **File Storage** | AWS S3 (SDK v2) for shop product files, or external links |
| **API Docs** | SpringDoc OpenAPI / Swagger UI |
| **Mapping** | MapStruct 1.5.5 |
| **Build Tool** | Gradle 9 |
| **Containerization** | Docker (multi-stage build) |

---

## Prerequisites

- **JDK 21** — [Eclipse Temurin](https://adoptium.net/) recommended
- **Docker & Docker Compose** — for the local PostgreSQL database
- A **Resend API key** — for transactional emails (activation, password reset, purchase confirmation)

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Avi-Dewan/Montola-School.git
cd Montola-School
```

### 2. Create your `.env` file

Create a `.env` file in the project root:

```env
# Database (used by both Docker Compose and the app)
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=montola
POSTGRES_HOST=localhost
POSTGRES_PORT=5432

# Server
PORT=8080

# Security
SECURITY_JWT_SECRET=your-secret-key-change-in-production
SECURITY_JWT_EXPIRATION_MINUTES=60

# Email (Resend)
RESEND_API_KEY=re_xxxxxxxxxxxx

# Frontend URL (for CORS and email links)
FRONTEND_URL=http://localhost:3000

# Shop product files (optional)
# "external" (default) keeps a supplied reference such as a Google Drive file id
# and needs no AWS credentials. Use "s3" to serve shop PDFs from a private bucket.
STORAGE_PROVIDER=external
AWS_REGION=ap-south-1
AWS_S3_BUCKET=montola-shop-files
AWS_S3_PREFIX=shop/
STORAGE_URL_TTL_SECONDS=300
# For "s3", credentials come from the standard AWS chain (env vars below, or an
# instance/task role in production). Never commit real keys.
# AWS_ACCESS_KEY_ID=
# AWS_SECRET_ACCESS_KEY=
```

### 3. Start the database

```bash
./dev-db.sh
```

This starts a PostgreSQL 16 container named `montola_db` with persistent storage.

### 4. Run the application

```bash
./dev-run.sh
```

The API will be available at **http://localhost:8080**

### 5. Explore the API

Open **http://localhost:8080/swagger-ui.html** for interactive API documentation.

---

## Run Scripts

The project provides **3 shell scripts** for different environments, plus a database script:

| Script | What It Does | When to Use |
|--------|-------------|-------------|
| `dev-db.sh` | Starts PostgreSQL 16 via Docker Compose using `.env` | **First step** — get the database running |
| `dev-run.sh` | Sets `SPRING_PROFILES_ACTIVE=dev`, sources `.env`, builds and runs via `./gradlew bootRun` | **Daily development** — local app against local DB |
| `docker-run.sh` | Builds a Docker image of the app and runs it as a container on the `montola-school_default` network | **Testing Docker deployment** — app + DB both in containers |
| `prod-run-local.sh` | Sets `SPRING_PROFILES_ACTIVE=prod`, sources `.env.prod`, runs via `./gradlew bootRun` | **Testing prod config** — local app against Neon (cloud) DB |

### Gradle Commands

You can also run Gradle tasks directly:

```bash
./gradlew clean build        # Compile + run tests
./gradlew bootRun            # Run the application
./gradlew test               # Run tests only
```

---

## Environment Variables

| Variable | Description | Default | Profile |
|----------|-------------|---------|---------|
| `PORT` | Server port | `8080` | All |
| `SPRING_PROFILES_ACTIVE` | Active profile (`dev` or `prod`) | — | All |
| `POSTGRES_USER` | Database username | `postgres` | Dev |
| `POSTGRES_PASSWORD` | Database password | `postgres` | Dev |
| `POSTGRES_DB` | Database name | `montola` | Dev |
| `POSTGRES_HOST` | Database host | `localhost` | Dev |
| `POSTGRES_PORT` | Database port | `5432` | Dev |
| `DB_URL` | Full JDBC URL (e.g., Neon connection string) | — | Prod |
| `SECURITY_JWT_SECRET` | JWT signing secret (HS256) | — | All |
| `SECURITY_JWT_EXPIRATION_MINUTES` | Access token expiry in minutes | `60` | All |
| `RESEND_API_KEY` | Resend email service API key | — | All |
| `FRONTEND_URL` | Frontend URL for CORS & email links | `http://localhost:3000` | All |
| `STORAGE_PROVIDER` | Shop file storage: `external` or `s3` | `external` | All |
| `AWS_S3_BUCKET` | Private S3 bucket for shop product files | — | All (s3) |
| `AWS_REGION` | AWS region for the bucket | `ap-south-1` | All (s3) |
| `AWS_S3_PREFIX` | Key prefix for uploaded objects | `shop/` | All (s3) |
| `STORAGE_URL_TTL_SECONDS` | Presigned download URL lifetime | `300` | All (s3) |
| `AWS_ACCESS_KEY_ID` / `AWS_SECRET_ACCESS_KEY` | AWS credentials (prefer an instance/task role in prod) | — | All (s3) |

### Dev vs Prod

| Aspect | Dev Profile | Prod Profile |
|--------|-------------|--------------|
| Database | Local Docker PostgreSQL | Neon cloud PostgreSQL (SSL) |
| Flyway | Enabled (baselined at V12 for pre-existing DBs) | Enabled (migrations applied) |
| DDL | `validate` | `validate` |
| Connection | Individual env vars | `DB_URL` with `?sslmode=require` |
| Pool | Default | HikariCP (max 5, min idle 1) |

---

## Project Structure

```
Montola-School/
├── dev-db.sh                          # Start dev PostgreSQL
├── dev-run.sh                         # Run in dev mode
├── docker-run.sh                      # Build & run Docker container
├── prod-run-local.sh                  # Run in prod mode locally
├── Dockerfile                         # Multi-stage build (JDK 21)
├── build.gradle                       # Dependencies & plugins
├── settings.gradle                    # Root project name
├── dev-setup/
│   └── docker-compose.yml             # PostgreSQL 16 container
└── src/main/
    ├── java/com/montola/school/
    │   ├── MontolaApplication.java    # Spring Boot entry point
    │   ├── admin/                     # Admin dashboard & statistics
    │   │   ├── controller/
    │   │   ├── dto/
    │   │   └── service/
    │   ├── auth/                      # Authentication & user management
    │   │   ├── controller/            # AuthController, UserController
    │   │   ├── dto/                   # 11 DTOs (login, register, tokens, etc.)
    │   │   ├── enums/                 # Role enum (ADMIN, MANAGER, TEACHER, STUDENT)
    │   │   ├── mapper/                # UserMapper (MapStruct)
    │   │   ├── model/                 # User, ActivationToken, RefreshToken, ResetPasswordToken
    │   │   ├── repository/            # 4 JPA repositories
    │   │   ├── security/              # CustomUserDetails, JpaUserDetailsService
    │   │   └── service/               # Auth, User, Token services
    │   ├── care/                      # Academic Care enquiry leads
    │   │   ├── controller/            # CareLeadController (public submit + admin)
    │   │   ├── dto/
    │   │   ├── enums/                 # CareLeadStatus
    │   │   ├── model/                 # CareLead
    │   │   ├── repository/
    │   │   └── service/
    │   ├── common/                    # Shared infrastructure
    │   │   ├── config/                # Security, OpenAPI, Resend, Message configs
    │   │   ├── dto/                   # ErrorResponse
    │   │   ├── exception/             # 11 custom exceptions + GlobalExceptionHandler
    │   │   ├── model/                 # Persistent base entity (audit fields, soft delete)
    │   │   ├── security/              # JWT filter, service, properties, SecurityUtils
    │   │   ├── service/               # BusinessEmailService
    │   │   └── storage/               # FileStorageService: S3 or external references
    │   ├── course/                    # Course content management
    │   │   ├── controller/            # 7 controllers (Class, Subject, Chapter, Topic, Content, Featured, Teacher)
    │   │   ├── dto/                   # 31 DTOs + 5 structure DTOs
    │   │   ├── enums/                 # ChapterStatus, ContentItemType, QuizType, etc.
    │   │   ├── mapper/                # 7 MapStruct mappers
    │   │   ├── model/                 # Entities + content subtypes (Lecture, Quiz, PDF) + Level
    │   │   ├── repository/            # 15 JPA repositories
    │   │   └── service/               # 12 service interfaces + implementations
    │   ├── learner/                   # Student progress & enrollment
    │   │   ├── controller/            # ProgressController
    │   │   ├── dto/
    │   │   ├── model/                 # ContentProgress, Enrollment
    │   │   ├── repository/
    │   │   └── service/
    │   ├── notice/                    # Homepage notices
    │   │   ├── controller/            # NoticeController (public read + admin)
    │   │   ├── dto/
    │   │   ├── enums/                 # NoticeType
    │   │   ├── model/                 # Notice
    │   │   ├── repository/
    │   │   └── service/
    │   ├── payment/                   # Chapter payment processing
    │   │   ├── controller/            # PaymentController, FreeEnrollmentController
    │   │   ├── dto/
    │   │   ├── enums/                 # PaymentStatus (PENDING, VERIFIED, REJECTED)
    │   │   ├── model/                 # Payment entity
    │   │   ├── repository/
    │   │   └── service/               # Payment + FreeEnrollment services
    │   └── shop/                      # Shop products, bundles, entitlements, payments
    │       ├── controller/            # Catalog, Purchase, Admin
    │       ├── dto/
    │       ├── enums/                 # ShopProductType, ProductFormat, BundleAudience, ...
    │       ├── model/                 # ShopProduct, ShopBundle, ShopEntitlement, ShopPayment
    │       ├── repository/
    │       └── service/               # Catalog, Access, Payment, Admin + DTO assembler
    └── resources/
        ├── application.yml            # Main config
        ├── application-dev.yml        # Dev profile (local DB, Flyway enabled)
        ├── application-prod.yml       # Prod profile (Neon DB, Flyway enabled)
        ├── messages.properties        # i18n error messages
        └── db/migration/              # Flyway SQL migrations (V1–V18)
```

---

## API Reference

### Health & Docs

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/internal/health` | Public | Health check (Actuator) |
| GET | `/swagger-ui.html` | Public | Swagger UI documentation |

### Authentication (`/api/auth`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/login` | Public | Login → returns JWT + refresh token |
| POST | `/logout` | Authenticated | Logout → deletes refresh token |
| POST | `/refresh-token` | Public | Refresh access token |
| POST | `/register` | Public | Register as student |
| POST | `/admin/register` | ADMIN | Register admin/manager/teacher |
| POST | `/activate` | Public | Activate account via email token |
| POST | `/resend-activation` | Public | Resend activation email |
| POST | `/change-password` | Authenticated | Change password |
| POST | `/forgot-password` | Public | Request password reset email |
| POST | `/reset-password` | Public | Reset password with token |

### User Management (`/api/users`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/` | ADMIN/MANAGER | List all users |
| GET | `/role/{role}` | ADMIN/MANAGER | List users by role |
| GET | `/{id}` | ADMIN/MANAGER/Self | Get user by ID |
| GET | `/email?email=` | ADMIN/MANAGER/Self | Get user by email |
| GET | `/exists?email=` | ADMIN/MANAGER | Check if email exists |
| POST | `/{id}/profile-picture` | ADMIN/MANAGER/Self | Upload profile picture |
| GET | `/{id}/profile-picture` | Public | Get profile picture |

### Classes (`/api/v1/classes`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/` | ADMIN/MANAGER | Create class |
| GET | `/` | Public | List all classes |
| GET | `/{id}` | Authenticated | Get class by ID |
| PUT | `/{id}` | ADMIN/MANAGER | Update class |
| DELETE | `/{id}` | ADMIN/MANAGER | Delete class |
| GET | `/{id}/structure` | Authenticated | Full tree structure |
| GET | `/{id}/public-structure` | Public | Public catalog view |

### Subjects (`/api/v1/subjects`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/` | ADMIN/MANAGER | Create subject |
| GET | `/` | ADMIN/MANAGER | List all subjects |
| GET | `/{id}` | Authenticated | Get subject by ID |
| PUT | `/{id}` | ADMIN/MANAGER | Update subject |
| DELETE | `/{id}` | ADMIN/MANAGER | Delete subject |
| GET | `/{id}/structure` | Authenticated | Subject structure tree |

### Chapters (`/api/v1/chapters`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/` | ADMIN/MANAGER | Create chapter |
| GET | `/` | ADMIN/MANAGER | List all chapters |
| GET | `/status/{status}` | ADMIN/MANAGER | Filter by status |
| GET | `/{id}` | ADMIN/MANAGER/TEACHER | Get chapter by ID |
| GET | `/{id}/public` | Public | Get public chapter info |
| GET | `/public/free` | Public | List free chapters |
| PUT | `/{id}` | ADMIN/MANAGER/TEACHER | Update chapter |
| DELETE | `/{id}` | ADMIN/MANAGER | Delete chapter |
| GET | `/{id}/structure` | Authenticated | Full chapter structure |
| GET | `/{id}/public-structure` | Public | Public chapter structure |
| POST | `/{id}/assign-teacher` | ADMIN/MANAGER | Assign teacher to chapter |
| DELETE | `/{id}/teachers/{teacherId}` | ADMIN/MANAGER | Unassign teacher |
| PATCH | `/{id}/status` | ADMIN/MANAGER | Update chapter status |
| PATCH | `/{id}/free-status` | ADMIN/MANAGER | Toggle free status |
| POST | `/{id}/cover-image` | ADMIN/MANAGER | Upload cover image |
| GET | `/{id}/cover-image` | Public | Get cover image |

### Topics (`/api/v1/topics`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/` | Authorized | Create topic |
| GET | `/` | Authenticated | List all topics |
| GET | `/{id}` | Authenticated | Get topic by ID |
| PUT | `/{id}` | Authorized | Update topic |
| DELETE | `/{id}` | Authorized | Delete topic |

> **Authorized** = Admin/Manager, or a Teacher assigned to the chapter.

### Content (`/api/v1/contents`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/{id}` | Enrolled | Get content by ID |
| POST | `/lecture` | Authorized | Create lecture |
| PUT | `/lecture/content-item/{id}` | Authorized | Update lecture |
| DELETE | `/lecture/content-item/{id}` | Authorized | Delete lecture |
| POST | `/quiz` | Authorized | Create quiz |
| PUT | `/quiz/{id}` | Authorized | Update quiz |
| PUT | `/quiz/{id}/questions` | Authorized | Update quiz questions |
| PUT | `/quiz/content-item/{id}` | Authorized | Update quiz by content item |
| DELETE | `/quiz/content-item/{id}` | Authorized | Delete quiz |
| PUT | `/quiz/content-item/{id}/questions` | Authorized | Update questions |
| POST | `/pdf` | Authorized | Create PDF content |
| PUT | `/pdf/content-item/{id}` | Authorized | Update PDF content |
| DELETE | `/pdf/content-item/{id}` | Authorized | Delete PDF content |

### Featured Chapters (`/api/v1/featured-chapters`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/` | Public | List featured chapters |
| POST | `/{chapterId}` | ADMIN/MANAGER | Add to featured |
| DELETE | `/{chapterId}` | ADMIN/MANAGER | Remove from featured |

### Teacher (`/api/v1/teachers`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/assigned-chapters` | TEACHER | Get assigned chapters |
| GET | `/chapters/{id}/statistics` | ADMIN/MANAGER/TEACHER | Chapter statistics |

### Payments (`/api/v1/payments`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/submit` | Authenticated | Submit payment (bKash/Nagad) |
| GET | `/my-payments` | Authenticated | Get my payments |
| GET | `/my-payments/chapter/{id}` | Authenticated | Payment for specific chapter |
| GET | `/` | ADMIN/MANAGER | List all payments |
| GET | `/unverified` | ADMIN/MANAGER | List unverified payments |
| PUT | `/{id}/verify` | ADMIN/MANAGER | Verify payment |
| PUT | `/{id}/reject` | ADMIN/MANAGER | Reject payment |

### Free Enrollment (`/api/v1/enrollments/free`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/{chapterId}` | Authenticated | Enroll in free chapter |

### Progress (`/api/v1/progress`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/content/{id}/complete` | Authenticated | Mark content complete |
| POST | `/content/{id}/quiz` | Authenticated | Submit quiz score |
| GET | `/chapter/{id}/detailed` | Authenticated | Detailed chapter progress |
| GET | `/chapter/{id}` | Authenticated | Chapter progress summary |
| GET | `/my-chapters` | Authenticated | All enrolled chapters |
| GET | `/admin/chapter/{id}/students-progress` | ADMIN/MANAGER/TEACHER | Students' progress |

### Shop (`/api/v1/shop`)

Products are individual items for sale (notes, PDFs, worksheets...); bundles are
packs of products. Access is granted by an *entitlement*, created when an admin
verifies a shop payment. Shop payments are separate from chapter payments.

**Catalog (public)**

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/levels` | Public | Curriculum levels (JSC/SSC/HSC) |
| GET | `/classes?levelId=` | Public | Classes of levels browsed per class (only JSC) |
| GET | `/products?type=&format=&levelId=&classId=&subjectId=&chapterId=` | Public | Published products |
| GET | `/products/{id}` | Public | Product detail (adds `entitled`/`canDownload` when a token is sent) |
| GET | `/featured` | Public | Featured published products |
| GET | `/bundles` | Public | Published bundles |
| GET | `/bundles/{id}` | Public | Bundle detail |

**Content & purchases (authenticated)**

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/products/{id}/content` | Entitled | Full content (interactive HTML or PDF reference) |
| GET | `/products/{id}/download` | Download-entitled | Time-limited download link |
| GET | `/my-purchases` | Authenticated | Everything the user owns |
| POST | `/payments/submit` | Authenticated | Submit a shop payment |
| GET | `/payments/my` | Authenticated | The user's shop payments |

**Admin (ADMIN/MANAGER)**

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/payments` | ADMIN/MANAGER | All shop payments |
| GET | `/payments/unverified` | ADMIN/MANAGER | Payments awaiting verification |
| PUT | `/payments/{id}/verify` | ADMIN/MANAGER | Verify → grants the entitlement |
| PUT | `/payments/{id}/reject` | ADMIN/MANAGER | Reject |
| GET | `/admin/products` | ADMIN/MANAGER | All products, including drafts |
| POST | `/admin/products` | ADMIN/MANAGER | Create product |
| PUT | `/admin/products/{id}` | ADMIN/MANAGER | Update product |
| DELETE | `/admin/products/{id}` | ADMIN/MANAGER | Soft-delete product |
| POST | `/admin/products/{id}/file` | ADMIN/MANAGER | Upload the product PDF (requires `STORAGE_PROVIDER=s3`) |
| GET | `/admin/bundles` | ADMIN/MANAGER | All bundles, including drafts |
| POST | `/admin/bundles` | ADMIN/MANAGER | Create bundle |
| PUT | `/admin/bundles/{id}` | ADMIN/MANAGER | Update bundle |
| DELETE | `/admin/bundles/{id}` | ADMIN/MANAGER | Soft-delete bundle |

**Access rules**

- Staff (ADMIN/MANAGER/TEACHER) may preview any product.
- A purchase grants an entitlement directly, or through a bundle containing the product.
- Downloads require a downloadable type (`WORKSHEET`, `DRILLSHEET`, `RECALL_CARD`), an
  entitlement through a bundle whose access mode is `DOWNLOAD`, and a TEACHER account.
- The payment amount is always taken from the catalog, never from the request body.

### Academic Care (`/api/v1/care`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/leads` | Public | Submit an enquiry (name + phone) |
| GET | `/admin/leads` | ADMIN/MANAGER | List enquiries |
| PUT | `/admin/leads/{id}` | ADMIN/MANAGER | Update follow-up status |
| DELETE | `/admin/leads/{id}` | ADMIN/MANAGER | Soft-delete an enquiry |

> This holds parents' names and phone numbers. Keep admin access restricted and
> consider a retention policy.

### Notices (`/api/v1/notices`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/` | Public | Active notices, ordered |
| GET | `/admin` | ADMIN/MANAGER | All notices, including hidden |
| POST | `/admin` | ADMIN/MANAGER | Create a notice |
| PUT | `/admin/{id}` | ADMIN/MANAGER | Update a notice |
| DELETE | `/admin/{id}` | ADMIN/MANAGER | Soft-delete a notice |

### Admin (`/api/v1/admin`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/statistics` | ADMIN | Dashboard statistics |

---

## Data Models

### Course Hierarchy

```
Level (JSC / SSC / HSC)
 └── Class
      └── Subject
           └── Chapter (has cover image, price, free flag, status)
                ├── Topic
                │    └── ContentItem
                │         ├── Lecture (video + rich text)
                │         ├── Quiz (MCQ, fill-blank, matching, written)
                │         └── GooglePdfContent (Google Drive PDF)
                └── ChapterTeacher (assignment join table)
```

A Level groups classes. `split_into_classes` records whether the level is browsed
per class (JSC: Class 6/7/8) or as a whole (SSC, HSC); the shop class tier uses it.
The chapter is the purchasable unit for courses.

### Shop

- **ShopProduct** — a sellable item: title, description, type, format, price, status,
  featured, preview, optional scope (level/class/subject/chapter), and its content
  (interactive HTML, or a file reference plus page count)
- **ShopBundle** — a pack of products, with an audience (`GENERAL` /
  `TEACHER_COACHING`) and access mode (`ONLINE` / `DOWNLOAD`)
- **ShopEntitlement** — a user's access to one product *or* one bundle (exactly one,
  enforced by a check constraint), with source and granted-at
- **ShopPayment** — a shop purchase (product or bundle) with
  `PENDING → VERIFIED / REJECTED`; verifying creates the entitlement

Product and bundle deletes are soft, so payment and entitlement history is retained.

### Academic Care

- **CareLead** — name, phone, level, area, message, status
  (`NEW → CONTACTED → ENROLLED / CLOSED`). Personal data.

### Notices

- **Notice** — title, message, type (`INFO` / `IMPORTANT` / `URGENT`), optional link,
  active flag and order index

### User & Auth

- **User** — email, fullName, passwordHash, phone, roles (Set), profilePhoto, isActivated
- **Roles** — `ADMIN`, `MANAGER`, `TEACHER`, `STUDENT` (a user can have multiple)
- **ActivationToken** — for email verification
- **RefreshToken** — for JWT refresh flow
- **ResetPasswordToken** — for password reset

### Learner

- **Enrollment** — links a user to a chapter with progress tracking
- **ContentProgress** — per-content completion status and quiz scores

### Payment

- **Payment** — senderNumber, transactionId, amount, method (BKASH/NAGAD), status (PENDING → VERIFIED/REJECTED)

### Base Entity

All entities extend `Persistent` which provides:
- `version` — optimistic locking
- `createdAt`, `updatedAt` — audit timestamps
- `isDeleted` — soft delete flag

---

## Authentication Flow

1. **Register** → User submits email/password → Account created (inactive) → Activation email sent
2. **Activate** → User clicks email link → Account activated
3. **Login** → Email/password verified → Returns access token (JWT, 60 min) + refresh token (DB-stored)
4. **Authenticated requests** → `Authorization: Bearer <access_token>` header
5. **Token refresh** → `POST /api/auth/refresh-token` with refresh token → New access token
6. **Logout** → Refresh token deleted from database

### Role-Based Access

| Role | Access Level |
|------|-------------|
| **ADMIN** | Full access to all endpoints |
| **MANAGER** | Same as Admin (shares admin dashboard) |
| **TEACHER** | Can edit assigned chapters, topics, and content |
| **STUDENT** | Can view purchased/free content, track progress |

---

## Database

### Development (Local)

PostgreSQL 16 runs in Docker. Start it with:

```bash
./dev-db.sh
```

This uses `dev-setup/docker-compose.yml` to create a container named `montola_db` with a persistent volume.

- **Flyway is enabled** in dev, running the same migrations as prod, so the two schemas cannot drift
- A database created before this change is baselined at V12, so only V13+ are applied to it;
  a fresh database gets the full `V1..V18` run
- Hibernate runs with `ddl-auto: validate` — it checks the schema but never creates it

### Production (Neon)

- Uses **Neon** cloud PostgreSQL with SSL (`?sslmode=require`)
- **Flyway is enabled** — runs migrations from `src/main/resources/db/migration/`
- 18 migration files organized in versioned folders (`init/`, `2026.1.1/`, `2026.3.1/`, `2026.4.1/`)
- Connection pool: HikariCP (max 5 connections, min 1 idle)

---

## File Storage (Shop)

Shop product files are stored behind a `FileStorageService` abstraction, so the
backing provider can change without touching shop logic.

| Provider | Behaviour | When to use |
|----------|-----------|-------------|
| `external` (default) | Keeps whatever reference is supplied (e.g. a Google Drive file id) and returns it unchanged | Local development, CI, or if you keep using Drive |
| `s3` | Stores uploaded files in a **private** S3 bucket and serves them via short-lived presigned URLs | Production |

To use S3:

1. Create a private bucket and leave **Block Public Access** enabled.
2. Enable server-side encryption (SSE-S3 is enough).
3. Grant the application least-privilege access — put, get and delete on the
   configured prefix only:

   ```json
   {
     "Version": "2012-10-17",
     "Statement": [{
       "Effect": "Allow",
       "Action": ["s3:PutObject", "s3:GetObject", "s3:DeleteObject"],
       "Resource": "arn:aws:s3:::montola-shop-files/shop/*"
     }]
   }
   ```

4. Set `STORAGE_PROVIDER=s3`, `AWS_S3_BUCKET` and `AWS_REGION`. Credentials come
   from the standard AWS chain — prefer an instance/task role over static keys.

**Cost:** S3 has no always-free tier; usage draws on Free Tier credits (new accounts
get $100–200 for 6 months). At this project's volume (a few GB, modest downloads) the
cost is around $0–1/month. Egress is the main cost, so front the bucket with
CloudFront when traffic grows — CloudFront includes 1 TB of egress per month,
always free, and swapping it in is a change to `FileStorageService` only.

The PDF variant of a product stores a reference (S3 key or external id) plus a page
count; downloading requires an entitlement through a `DOWNLOAD` bundle and a TEACHER
account. See the [Shop API](#shop-apiv1shop) section for the access rules.

---

## API Documentation

Interactive API documentation is available via **Swagger UI**:

```
http://localhost:8080/swagger-ui.html
```

All endpoints are documented with OpenAPI annotations. Use the "Authorize" button in Swagger UI to set your JWT token for authenticated requests.

---

## Docker

### Multi-Stage Build

The `Dockerfile` uses a 2-stage build:

1. **Build stage** — `gradle:8.5-jdk21`, compiles the app (tests skipped)
2. **Run stage** — `eclipse-temurin:21-jre-alpine`, runs the JAR

### Running in Docker

```bash
# Make sure the DB is running first
./dev-db.sh

# Build image and run container
./docker-run.sh
```

The container runs on port 8080 and connects to `montola_db` on the Docker network.

---

## Frontend Integration

The frontend runs at **http://localhost:3000** and communicates with this API at **http://localhost:8080/api**.

- **CORS** is configured via the `FRONTEND_URL` environment variable
- **Images** are served directly from API endpoints (cover images, profile pictures)
- See the [Frontend README](https://github.com/Avi-Dewan/Montola-School-FrontEnd) for setup instructions

---

## License

Private project — all rights reserved.
