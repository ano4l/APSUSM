# APSUSM Membership Registration & Card Generation System

Full-stack system for APSUSM (Associação dos Profissionais de Saúde Unidos e Solidários de Moçambique) membership registration, payment processing, and digital card generation.

## Architecture

```
apsusm-backend/   → Spring Boot 3.2 (Java 17) REST API
apsusm-frontend/  → React 18 + Vite + Tailwind CSS
```

## Features

- **Member Registration** — Form with photo upload, frontend + backend validation
- **Paystack Payment** — Secure payment gateway with webhook verification (server-side)
- **Unique Member ID** — Format: `APSUSM-YYYY-XXXX` (sequential, non-duplicated)
- **Card Generation** — Java2D engine renders front + back PNG cards with photo, QR code, branding
- **Email Confirmation** — Sends member ID, payment receipt, and card attachments
- **Public Verification** — QR code on card links to `/verify/{memberId}` page
- **Admin Dashboard** — View all members, filter by status, regenerate cards, download cards

## Prerequisites

- **Java 17+** (JDK)
- **Maven 3.8+**
- **Node.js 18+** and **npm**
- **Paystack account** (test keys work for development)

## Quick Start

### 1. Backend

```bash
cd apsusm-backend

# Configure environment (edit src/main/resources/application.properties)
# Set your Paystack keys, email SMTP credentials, etc.

# Build and run
mvn spring-boot:run
```

The backend starts on **http://localhost:8080**.

H2 Console available at: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./data/apsusm_db`
- Username: `sa` / Password: (empty)

### 2. Frontend

```bash
cd apsusm-frontend

npm install
npm run dev
```

The frontend starts on **http://localhost:5173** and proxies API calls to the backend.

## Configuration

All config is in `apsusm-backend/src/main/resources/application.properties`:

| Property | Description | Default |
|---|---|---|
| `paystack.secret-key` | Paystack secret key | `sk_test_xxx` |
| `paystack.public-key` | Paystack public key | `pk_test_xxx` |
| `paystack.callback-url` | Redirect after payment | `http://localhost:5173/payment/verify` |
| `app.membership.fee` | Fee in smallest unit (kobo/centavos) | `500000` |
| `app.membership.currency` | Currency code | `MZN` |
| `spring.mail.host` | SMTP host | `smtp.gmail.com` |
| `spring.mail.username` | SMTP username | — |
| `spring.mail.password` | SMTP password (app password) | — |
| `app.admin.username` | Admin login username | `admin` |
| `app.admin.password` | Admin login password | `apsusm2024` |

Environment variables override properties: `PAYSTACK_SECRET_KEY`, `PAYSTACK_PUBLIC_KEY`, `MAIL_HOST`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `ADMIN_USERNAME`, `ADMIN_PASSWORD`.

## API Endpoints

### Public

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/members/register` | Register (multipart form + photo) |
| `POST` | `/api/members/{id}/pay` | Initialize Paystack payment |
| `GET` | `/api/payment/verify/{reference}` | Verify payment after redirect |
| `GET` | `/api/members/status/{id}` | Check member status |
| `GET` | `/api/members/verify/{memberId}` | Public verification |
| `GET` | `/api/members/card/{id}/front` | Download card front PNG |
| `GET` | `/api/members/card/{id}/back` | Download card back PNG |

### Webhook

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/webhooks/paystack` | Paystack webhook (HMAC-SHA512 verified) |

### Admin (Basic Auth)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/admin/dashboard` | Stats overview |
| `GET` | `/api/admin/members` | List all members (?status=ACTIVE) |
| `GET` | `/api/admin/members/{id}` | Single member detail |
| `POST` | `/api/admin/members/{id}/regenerate-card` | Regenerate card |

## Registration Flow

1. User fills form + uploads portrait photo → `POST /api/members/register`
2. Backend validates, stores photo, saves member with `PENDING_PAYMENT`
3. Frontend calls `POST /api/members/{id}/pay` → Paystack authorization URL
4. User pays on Paystack → redirected to `/payment/verify?reference=...`
5. Paystack webhook `POST /api/webhooks/paystack` verifies payment server-side
6. On success: generate unique `APSUSM-YYYY-XXXX` ID, set status `PAID`
7. Card generation engine renders front + back PNG with photo, QR code, branding
8. Email sent with payment confirmation, member ID, and card attachments
9. Status updated to `ACTIVE`

## Production Deployment

1. Switch database to PostgreSQL (uncomment config in `application.properties`)
2. Set real Paystack live keys
3. Configure SMTP for production email
4. Set `paystack.callback-url` to your production domain
5. Set up Paystack webhook URL to `https://yourdomain.com/api/webhooks/paystack`
6. Build frontend: `npm run build` → serve from Spring Boot static or Nginx
7. Build backend: `mvn clean package` → run JAR

```bash
# Production build
cd apsusm-backend && mvn clean package -DskipTests
java -jar target/apsusm-membership-1.0.0.jar
```

## Card Design

- **Front**: Dark (#0a0a0a) background, APSUSM logo, smart card chip, portrait photo, name, specialization, license number, member ID, province, issue/expiry dates, bottom gradient bar (blue→red→green)
- **Back**: Light background, QR code (links to verification URL), organization details, member info, issue/expiry dates, security notice, contact info, bottom gradient bar
# APSUSM
