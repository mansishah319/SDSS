# SDSS — Strategic Decision Support System

Monorepo containing the full SDSS stack:

| Folder | Stack | Port |
|--------|-------|------|
| `api/` | Spring Boot 2.3.12 (Java 17) | `8082` |
| `frontend/` | Vue 3 + Vite + TypeScript + Tailwind | `5173` |

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 17 |
| Maven | 3.8+ |
| Node.js | 20+ |
| pnpm | 8+ |
| Docker | any recent version |

Install pnpm if you don't have it:
```bash
npm install -g pnpm
```

---

## 1. Start Oracle Database (Docker)

```bash
docker run -d \
  --name sfg-oracle \
  -p 1521:1521 \
  -e ORACLE_PASSWORD=SysPassword1 \
  container-registry.oracle.com/database/free:latest
```

Wait ~2 minutes until the DB is ready:
```bash
docker logs sfg-oracle | tail -5
# Should show: DATABASE IS READY TO USE!
```

### Create the three schemas

```bash
docker exec sfg-oracle bash -c "echo \"
CREATE USER dphq_sdss IDENTIFIED BY DPHQ_SDSS DEFAULT TABLESPACE USERS QUOTA UNLIMITED ON USERS;
GRANT CONNECT, RESOURCE, CREATE VIEW, CREATE SYNONYM TO dphq_sdss;
CREATE USER weblisher IDENTIFIED BY weblisher DEFAULT TABLESPACE USERS QUOTA UNLIMITED ON USERS;
GRANT CONNECT, RESOURCE, CREATE VIEW, CREATE SYNONYM TO weblisher;
CREATE USER committees IDENTIFIED BY committees DEFAULT TABLESPACE USERS QUOTA UNLIMITED ON USERS;
GRANT CONNECT, RESOURCE, CREATE VIEW, CREATE SYNONYM TO committees;
COMMIT;
\" | sqlplus -s sys/SysPassword1@//localhost:1521/FREEPDB1 as sysdba"
```

### Seed required data

Connect as `dphq_sdss` and run:

```sql
-- Admin user
INSERT INTO DPHQ_SDSS.USERS (USERNAME, EMAIL, GRP_NUMBER, LANGUAGE, STATUS,
  RATINGS, DECISION, QUESTION_VIEW, CHAT_CONTENTS, SCROLLING_TEXT,
  SOCIAL_MEDIA_FEEDS, INTERNAL_DOCUMENTS, CREATED_AT)
VALUES ('admin', 'admin@dubaipolice.gov.ae', 696797, 1, 1, 1, 1, 1, 1, 1, 1, 1, SYSTIMESTAMP);

-- Roles
INSERT INTO DPHQ_SDSS.ROLES_ENTITY (NAME_EN, NAME_AR, STATUS, ACTIVE, CREATED_AT, CREATED_BY) VALUES ('PORTALGUEST',        'ضيف',  'ACTIVE', 1, SYSTIMESTAMP, 'system');
INSERT INTO DPHQ_SDSS.ROLES_ENTITY (NAME_EN, NAME_AR, STATUS, ACTIVE, CREATED_AT, CREATED_BY) VALUES ('PORTALADMIN',        'أدمن', 'ACTIVE', 1, SYSTIMESTAMP, 'system');
INSERT INTO DPHQ_SDSS.ROLES_ENTITY (NAME_EN, NAME_AR, STATUS, ACTIVE, CREATED_AT, CREATED_BY) VALUES ('PORTALROLEMEMBER',   'عضو',  'ACTIVE', 1, SYSTIMESTAMP, 'system');
INSERT INTO DPHQ_SDSS.ROLES_ENTITY (NAME_EN, NAME_AR, STATUS, ACTIVE, CREATED_AT, CREATED_BY) VALUES ('PORTALROLEVALIDATOR','مدقق', 'ACTIVE', 1, SYSTIMESTAMP, 'system');
INSERT INTO DPHQ_SDSS.ROLES_ENTITY (NAME_EN, NAME_AR, STATUS, ACTIVE, CREATED_AT, CREATED_BY) VALUES ('PORTALROLECOMMANDER','مدير', 'ACTIVE', 1, SYSTIMESTAMP, 'system');
INSERT INTO DPHQ_SDSS.ROLES_ENTITY (NAME_EN, NAME_AR, STATUS, ACTIVE, CREATED_AT, CREATED_BY) VALUES ('PORTALROLEMODERATOR','منسق', 'ACTIVE', 1, SYSTIMESTAMP, 'system');
COMMIT;
```

Connect as `weblisher` and run:

```sql
-- Sector and department hierarchy
INSERT INTO WEBLISHER.DP_HR_DEPARTMENTS_GRP
  (SUB_ORGANIZATION_ID, SUB_ORGANIZATION_TYPE_CODE, PARENT_ORGANIZATION_ID, PARENT_ORGANIZATION_TYPE_CODE, SUBORDINATE_ORGANIZATION, DEP_ORDER)
VALUES (100, 70, NULL, 90, 'IT Sector', 1);

INSERT INTO WEBLISHER.DP_HR_DEPARTMENTS_GRP
  (SUB_ORGANIZATION_ID, SUB_ORGANIZATION_TYPE_CODE, PARENT_ORGANIZATION_ID, PARENT_ORGANIZATION_TYPE_CODE, SUBORDINATE_ORGANIZATION, DEP_ORDER)
VALUES (1, 1, 100, 70, 'IT Department', 1);

-- Employee record for admin
INSERT INTO WEBLISHER.DP_EMP_MV_GRP
  (GRP_SERL_NO, PERSON_ID, "A$LAST_NAME", PERSON_START_DATE, ASSIGNMENT_STATUS_ID,
   FIRST_NAME, LAST_NAME, FULL_NAME, EMAIL_ADDRESS, EMPLOYEE_NUMBER)
VALUES (696797, 696797, 'Admin', SYSTIMESTAMP, 1, 'Admin', 'Admin', 'Admin Admin',
        'admin@dubaipolice.gov.ae', '696797');
COMMIT;
```

---

## 2. Run the Backend (api/)

```bash
cd api
mvn clean package -DskipTests
java -jar target/sdss-0.0.1-SNAPSHOT.war --spring.profiles.active=local
```

Verify it's up:
```bash
curl http://localhost:8082/sdss-service/actuator/health
# {"status":"UP"}
```

Swagger UI: [http://localhost:8082/sdss-service/DubaiPolice/swagger-ui.html](http://localhost:8082/sdss-service/DubaiPolice/swagger-ui.html)

---

## 3. Run the Frontend (frontend/)

### Configure environment

Create `frontend/.env.local`:
```env
VITE_SERVER_URL=http://localhost:8082/sdss-service
```

### Install and start

```bash
cd frontend
pnpm install
pnpm dev
```

The app runs at [http://localhost:5173](http://localhost:5173).

### Build for production

```bash
pnpm build
# Output in frontend/dist/
```

---

## 4. Generate a local JWT (for API testing)

The local admin JWT secret (base64-encoded HS512 key):
```
bXktc2VjcmV0LWtleS13aGljaC1zaG91bGQtYmUtY2hhbmdlZC1pbi1wcm9kdWN0aW9uLWFuZC1iZS1iYXNlNjQtZW5jb2RlZAo=
```

Use the generation script:
```bash
node api/scratchpad/gen_jwt.js
```

> **Important:** The JWT must use `"roles": ["PORTALADMIN"]` — not `"ADMIN"`. Using `"ADMIN"` causes 401 ERR_030 on department and lookup endpoints.

Test the API:
```bash
JWT=<token>

curl "http://localhost:8082/sdss-service/api/v1/users/search?page=0&size=10" \
  -H "Authorization: Bearer $JWT"

curl "http://localhost:8082/sdss-service/api/v1/challenges/search?page=0&size=5" \
  -H "Authorization: Bearer $JWT"

curl "http://localhost:8082/sdss-service/api/v1/lookup/users/list" \
  -H "Authorization: Bearer $JWT"
```

---

## Project Structure

```
.
├── api/                        # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/           # Java source code
│   │   │   └── resources/      # application.yml, i18n, templates
│   │   └── test/
│   └── pom.xml
│
└── frontend/                   # Vue 3 frontend
    ├── src/
    │   ├── components/
    │   ├── views/
    │   ├── services/
    │   └── plugins/
    ├── public/
    ├── index.html
    ├── vite.config.ts
    └── package.json
```

---

## Environment Variables

### Backend (`api/`)

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | `8082` | HTTP port |
| `SECURITY_JWT_SECRET` | *(see config)* | Base64 HMAC-SHA512 signing key |
| `HIBERNATE_DDL_AUTO` | `update` | Schema strategy |
| `LOGGING_ROOT` | `INFO` | Root log level |

### Frontend (`frontend/`)

| Variable | Description |
|----------|-------------|
| `VITE_SERVER_URL` | Backend base URL (e.g. `http://localhost:8082/sdss-service`) |
| `VITE_IDS_BASE_URL` | Identity server base URL (WSO2) |

---

## Enable Debug Logging (runtime)

```bash
curl -X POST http://localhost:8082/sdss-service/actuator/loggers/com.mgdsw.sdss \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'
```
