# APSUSM Membership Registration and Card Generation System

APSUSM is a monorepo with three deployable services:

- `apsusm-backend/` - Spring Boot 3.2 REST API
- `apsusm-frontend/` - React 18 + Vite frontend
- `card-generator/` - Python service for card rendering

The current production flow is member registration without online payment. A successful registration creates the member record, generates the card, and sends the confirmation email.

## Features

- Photo-based member registration
- Immediate member ID assignment
- Front and back digital card generation
- Email confirmation with card attachments
- Public card verification by member ID
- Admin dashboard for member review and card regeneration

## Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 18+
- Python 3.11+
- SMTP credentials for confirmation emails

## Local Development

### Backend

```bash
cd apsusm-backend
mvn spring-boot:run
```

### Frontend

```bash
cd apsusm-frontend
npm install
npm run dev
```

### Card generator

```bash
cd card-generator
pip install -r requirements.txt
python app.py
```

## Backend configuration

Key backend environment variables:

- `PORT`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_DATASOURCE_DRIVERCLASSNAME`
- `SPRING_JPA_DATABASE_PLATFORM`
- `SPRING_JPA_HIBERNATE_DDL_AUTO`
- `APP_CORS_ALLOWED_ORIGINS`
- `APP_CARD_GENERATOR_URL`
- `MAIL_HOST`
- `MAIL_PORT`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `MAIL_FROM`
- `ADMIN_USERNAME`
- `ADMIN_PASSWORD`

## Public API

- `POST /api/members/register` - Register a member with multipart form data and photo upload
- `GET /api/members/status/{id}` - Check registration status
- `GET /api/members/verify/{memberId}` - Verify a member publicly
- `GET /api/members/card/{id}/front` - Download the front card
- `GET /api/members/card/{id}/back` - Download the back card

## Registration flow

1. User submits the registration form and photo.
2. Backend validates the request and stores the uploaded photo.
3. A unique member ID is assigned.
4. Card assets are generated through the card generator service.
5. Confirmation email is sent.
6. Member status moves to `ACTIVE`.

## Deployment

Railway is the active deployment target. See `DEPLOY_RAILWAY.md` for the per-service setup.
