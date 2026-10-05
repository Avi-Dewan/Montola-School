# Frontend Contract — Shop, Academic Care & Notices

The frontend already calls these endpoints (see the frontend repo's
`src/lib/{shop,care,notices}.ts` and `src/types/shop.ts`). This document records
the exact JSON shapes it expects, so backend changes do not silently break it.

Base URL is `.../api`. All shop paths are under `/api/v1/shop`.

---

## Field-name rules

These are easy to get wrong and will break the UI silently:

- **`free`**, not `isFree`. Jackson serialises a boolean getter `isFree()` as `free`.
  The same applies to other booleans on existing entities.
- Shop booleans are named **without** an `is` prefix in DTOs (`featured`,
  `downloadable`, `active`) so the JSON keys come out as written.
- Enums are serialised as their **name** (`PUBLISHED`, `WORKSHEET`, `DOWNLOAD`).

---

## Shop

### Product card — `ShopProductCardDto`

Returned by `/products`, `/featured`, `/admin/products`, and nested inside bundles
and purchases.

```json
{
  "id": 5001,
  "title": "Algebra — infographic notes",
  "description": "Visual notes covering variables and linear equations.",
  "type": "NOTES",
  "format": "INTERACTIVE",
  "price": 80,
  "status": "PUBLISHED",
  "featured": true,
  "preview": "Preview: a variable is a placeholder…",
  "downloadable": false,
  "levelId": 2, "levelName": "SSC",
  "classId": 9, "className": "Class 9",
  "subjectId": 10, "subjectName": "Mathematics",
  "chapterId": 100, "chapterTitle": "Algebra Basics"
}
```

- `downloadable` is derived: `type` is `WORKSHEET`, `DRILLSHEET` or `RECALL_CARD`.
- Scope fields are all nullable — a product may be tied to any level of the
  curriculum or to none.
- Only `PUBLISHED` products appear in the public catalog; `/admin/products` includes drafts.

### Product detail — `ShopProductDetailDto`

`GET /products/{id}` — the card plus two per-viewer flags:

```json
{ "...card fields...", "entitled": false, "canDownload": false }
```

The endpoint is **public** but reads an optional token, so anonymous visitors get
`false` for both. An invalid or expired token must not cause an error here — the
request stays public.

### Product content — `GET /products/{id}/content` (entitled)

```json
{
  "id": 5001, "title": "Algebra — infographic notes",
  "type": "NOTES", "format": "INTERACTIVE",
  "watermark": "Licensed to student@test.local — Montola School",
  "html": "<h2>Algebra</h2><p>…</p>",
  "fileId": null, "pageCount": null
}
```

`html` is set for `INTERACTIVE` products; `fileId`/`pageCount` for `PDF` ones.
403 with message `shop.purchase.required` when not entitled.

### Download — `GET /products/{id}/download` (download-entitled)

```json
{
  "fileId": "shop/<uuid>-trig-worksheet.pdf",
  "url": "https://…presigned…",
  "expiresInSeconds": 300,
  "watermark": "student@test.local · 5"
}
```

403 with `shop.download.not.permitted` otherwise.

### Bundle — `ShopBundleDto`

```json
{
  "id": 7001,
  "title": "SSC practice pack",
  "description": "Download-and-print pack for classrooms.",
  "audience": "TEACHER_COACHING",
  "accessMode": "DOWNLOAD",
  "price": 90,
  "status": "PUBLISHED",
  "levelId": 2, "levelName": "SSC",
  "subjectId": null, "subjectName": null,
  "productCount": 3,
  "products": [ /* product cards */ ]
}
```

### Purchases — `GET /my-purchases`

An array where each element is a product or a bundle, distinguished by `kind`:

```json
[
  { "kind": "PRODUCT", "grantedAt": "2026-06-10T10:00:00", "...card fields..." },
  { "kind": "BUNDLE",  "grantedAt": "2026-06-12T10:00:00", "...bundle fields..." }
]
```

### Payment — `ShopPaymentDto`

```json
{
  "id": 9101, "userId": 5, "userName": null,
  "productId": 5001, "productTitle": "Algebra — infographic notes",
  "bundleId": null, "bundleTitle": null,
  "itemTitle": "Algebra — infographic notes",
  "amount": 80, "senderNumber": "01711111111",
  "transactionId": "SHOPTEST1", "paymentMethod": "BKASH",
  "status": "PENDING", "verifiedAt": null
}
```

`userName` is populated for admin listings only.

### Payment submission — `POST /payments/submit`

Request:

```json
{ "productId": 5001, "amount": 80, "senderNumber": "01711111111",
  "transactionId": "SHOPTEST1", "paymentMethod": "BKASH" }
```

Exactly one of `productId` / `bundleId` is required; the amount is ignored and taken
from the catalog. `senderNumber` must match `^(01)[3-9][0-9]{8}$`. Responds `201`.

### Levels & classes

`GET /levels` → `[{ "id": 1, "name": "JSC", "orderIndex": 0 }]`

`GET /classes?levelId=` → `[{ "id": 6, "name": "Class 6", "levelId": 1, "orderIndex": 0 }]`

**Only levels that split into classes return rows** — JSC returns Class 6/7/8, while
SSC and HSC return `[]`. The frontend then shows its "browse the whole level" card, so
returning classes for SSC/HSC would change the UI.

---

## Academic Care

`POST /care/leads` (public) — request:

```json
{ "name": "Rahima Begum", "phone": "01711223344",
  "level": "Class 6-8", "area": "Milanpur", "message": "Weak in Math" }
```

Responds `201` with `{ "ok": true, "id": 4001 }`. Invalid input returns `400` with the
standard `ErrorResponse` (`fieldErrors` populated), which the form surfaces.

`GET /care/admin/leads` returns an array of leads, newest first, each with
`id, name, phone, level, area, message, status, createdAt`.

---

## Notices

`GET /notices` (public) — active notices, ordered:

```json
[{ "id": 3001, "title": "Admission open for the new batch",
   "message": "…", "type": "IMPORTANT", "link": "/classes",
   "active": true, "orderIndex": 0, "createdAt": "2026-06-28T10:00:00" }]
```

`type` is `INFO` | `IMPORTANT` | `URGENT`. The frontend renders nothing when the list
is empty, so an empty array is a valid, safe response.

---

## Error responses

All errors use the existing `ErrorResponse`:

```json
{ "status": 403, "message": "Purchase required to view this product",
  "timestamp": "2026-07-01T10:00:00", "fieldErrors": null }
```

The frontend shows `err.response.data.message`, so messages must be human-readable.
