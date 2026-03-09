# Deploy APSUSM on Railway

This repo is now prepared for Railway with per-service config files:
- `apsusm-backend/railway.json`
- `card-generator/railway.json`
- `apsusm-frontend/railway.json`

## 1) Create services in one Railway project

In Railway, create **one Project** and add these resources:
1. **PostgreSQL** database
2. **Web Service** from `apsusm-backend`
3. **Web Service** from `card-generator`
4. **Web Service** from `apsusm-frontend`

When creating each service, set the **Root Directory** to the folder above.
Railway will use each folder's `railway.json` automatically.

## 2) Backend (`apsusm-backend`) variables

Set these in backend service Variables:

Required:
- `PORT=${{PORT}}` is provided by Railway automatically; the app now reads it via `server.port=${PORT:5445}`
- `SPRING_DATASOURCE_URL=jdbc:postgresql://<PGHOST>:<PGPORT>/<PGDATABASE>`
- `SPRING_DATASOURCE_USERNAME=<PGUSER>`
- `SPRING_DATASOURCE_PASSWORD=<PGPASSWORD>`
- `SPRING_DATASOURCE_DRIVERCLASSNAME=org.postgresql.Driver`
- `SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect`
- `SPRING_JPA_HIBERNATE_DDL_AUTO=update`
- `SPRING_H2_CONSOLE_ENABLED=false`
- `APP_CORS_ALLOWED_ORIGINS=https://<your-frontend-domain>.up.railway.app`
- `APP_CARD_GENERATOR_URL=https://<your-card-generator-domain>.up.railway.app`

Secrets:
- `MAIL_HOST`
- `MAIL_PORT` (usually `587`)
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `MAIL_FROM`
- `ADMIN_USERNAME`
- `ADMIN_PASSWORD`

## 3) Card generator (`card-generator`) variables

Set:
- `OPENAI_API_KEY`

Optional:
- `CARD_TEMPLATE`
- `CARD_EXAMPLE`
- `CARD_TEMPLATE_BACK`

Health check endpoint:
- `GET /api/health`

## 4) Frontend (`apsusm-frontend`) variables

Set:
- `VITE_API_BASE_URL=https://<your-backend-domain>.up.railway.app/api`

## 5) Current registration flow

- Member registration currently completes without an online payment step.
- Keep the Paystack config out of Railway unless you decide to re-enable payment later.
- If you re-enable Paystack later, add callback and webhook URLs back at that time.

## 6) Smoke test

1. Open frontend URL
2. Submit registration
3. Confirm backend logs show registration, card generation, and email processing
4. Confirm card generator health endpoint is OK
5. Open the success page and confirm card downloads work

## Notes

- Frontend now supports env-based backend URL via `VITE_API_BASE_URL`.
- Backend now reads `PORT` and `APP_CORS_ALLOWED_ORIGINS` from Railway environment variables.
- Local development still works with `/api` fallback in `apsusm-frontend/src/api.js`.
