# SDSS — Strategic Decision Support System

Spring Boot backend service for the SDSS portal.

- **Java**: 1.8+  (runs fine on Java 17)
- **Spring Boot**: 2.3.12
- **Database**: Oracle (3 schemas: `dphq_sdss`, `weblisher`, `committees`)
- **Context path**: `/sdss-service`
- **Default port**: `8082`

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 17 (recommended) |
| Maven | 3.8+ |
| Docker | any recent version |
| Oracle Free 23c | via Docker (see below) |

---

## 1. Start Oracle Database

```bash
docker run -d \
  --name sfg-oracle \
  -p 1521:1521 \
  -e ORACLE_PASSWORD=SysPassword1 \
  container-registry.oracle.com/database/free:latest
```

Wait ~2 minutes for the DB to be ready, then check:

```bash
docker logs sfg-oracle | tail -5
```

You should see `DATABASE IS READY TO USE!`.

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

---

## 2. Seed required reference data

After the schemas are created, run the following SQL as `dphq_sdss` to seed the minimum required data.

### Admin user

```sql
-- Connect as dphq_sdss
INSERT INTO DPHQ_SDSS.USERS (USERNAME, EMAIL, GRP_NUMBER, LANGUAGE, STATUS, RATINGS, DECISION, QUESTION_VIEW, CHAT_CONTENTS, SCROLLING_TEXT, SOCIAL_MEDIA_FEEDS, INTERNAL_DOCUMENTS, CREATED_AT)
VALUES ('admin', 'admin@dubaipolice.gov.ae', 696797, 1, 1, 1, 1, 1, 1, 1, 1, 1, SYSTIMESTAMP);
COMMIT;
```

### Roles

```sql
INSERT INTO DPHQ_SDSS.ROLES_ENTITY (NAME_EN, NAME_AR, STATUS, ACTIVE, CREATED_AT, CREATED_BY) VALUES ('PORTALGUEST',        'ضيف',   'ACTIVE', 1, SYSTIMESTAMP, 'system');
INSERT INTO DPHQ_SDSS.ROLES_ENTITY (NAME_EN, NAME_AR, STATUS, ACTIVE, CREATED_AT, CREATED_BY) VALUES ('PORTALADMIN',        'أدمن',  'ACTIVE', 1, SYSTIMESTAMP, 'system');
INSERT INTO DPHQ_SDSS.ROLES_ENTITY (NAME_EN, NAME_AR, STATUS, ACTIVE, CREATED_AT, CREATED_BY) VALUES ('PORTALROLEMEMBER',   'عضو',   'ACTIVE', 1, SYSTIMESTAMP, 'system');
INSERT INTO DPHQ_SDSS.ROLES_ENTITY (NAME_EN, NAME_AR, STATUS, ACTIVE, CREATED_AT, CREATED_BY) VALUES ('PORTALROLEVALIDATOR','مدقق',  'ACTIVE', 1, SYSTIMESTAMP, 'system');
INSERT INTO DPHQ_SDSS.ROLES_ENTITY (NAME_EN, NAME_AR, STATUS, ACTIVE, CREATED_AT, CREATED_BY) VALUES ('PORTALROLECOMMANDER','مدير',  'ACTIVE', 1, SYSTIMESTAMP, 'system');
INSERT INTO DPHQ_SDSS.ROLES_ENTITY (NAME_EN, NAME_AR, STATUS, ACTIVE, CREATED_AT, CREATED_BY) VALUES ('PORTALROLEMODERATOR','منسق',  'ACTIVE', 1, SYSTIMESTAMP, 'system');
COMMIT;
```

### Department hierarchy (weblisher schema)

```sql
-- Connect as weblisher
INSERT INTO WEBLISHER.DP_HR_DEPARTMENTS_GRP
  (SUB_ORGANIZATION_ID, SUB_ORGANIZATION_TYPE_CODE, PARENT_ORGANIZATION_ID, PARENT_ORGANIZATION_TYPE_CODE, SUBORDINATE_ORGANIZATION, DEP_ORDER)
VALUES (100, 70, NULL, 90, 'IT Sector', 1);

INSERT INTO WEBLISHER.DP_HR_DEPARTMENTS_GRP
  (SUB_ORGANIZATION_ID, SUB_ORGANIZATION_TYPE_CODE, PARENT_ORGANIZATION_ID, PARENT_ORGANIZATION_TYPE_CODE, SUBORDINATE_ORGANIZATION, DEP_ORDER)
VALUES (1, 1, 100, 70, 'IT Department', 1);

COMMIT;
```

### Employee record (weblisher schema)

```sql
INSERT INTO WEBLISHER.DP_EMP_MV_GRP
  (GRP_SERL_NO, PERSON_ID, "A$LAST_NAME", PERSON_START_DATE, ASSIGNMENT_STATUS_ID, FIRST_NAME, LAST_NAME, FULL_NAME, EMAIL_ADDRESS, EMPLOYEE_NUMBER)
VALUES (696797, 696797, 'Admin', SYSTIMESTAMP, 1, 'Admin', 'Admin', 'Admin Admin', 'admin@dubaipolice.gov.ae', '696797');
COMMIT;
```

---

## 3. Build the application

```bash
cd sdss
mvn clean package -DskipTests
```

---

## 4. Run the application

```bash
java -jar target/sdss-0.0.1-SNAPSHOT.war \
  --spring.profiles.active=local
```

The service starts on `http://localhost:8082/sdss-service`.

### Verify it's up

```bash
curl http://localhost:8082/sdss-service/actuator/health
```

Expected: `{"status":"UP"}`

---

## 5. Generate a local JWT

The local JWT secret (base64-encoded) is:

```
bXktc2VjcmV0LWtleS13aGljaC1zaG91bGQtYmUtY2hhbmdlZC1pbi1wcm9kdWN0aW9uLWFuZC1iZS1iYXNlNjQtZW5jb2RlZAo=
```

Use the script at `scratchpad/gen_jwt.js` to generate a signed admin token:

```bash
node scratchpad/gen_jwt.js
```

The JWT payload uses `"roles": ["PORTALADMIN"]` — this is required for admin endpoints. Using `"ADMIN"` will cause 401 ERR_030 on department/lookup endpoints.

### Test with the token

```bash
JWT=<token from above>

curl "http://localhost:8082/sdss-service/api/v1/users/search?page=0&size=10" \
  -H "Authorization: Bearer $JWT"

curl "http://localhost:8082/sdss-service/api/v1/challenges/search?page=0&size=5" \
  -H "Authorization: Bearer $JWT"

curl "http://localhost:8082/sdss-service/api/v1/lookup/users/list" \
  -H "Authorization: Bearer $JWT"
```

---

## API Documentation

Swagger UI is available at:

```
http://localhost:8082/sdss-service/DubaiPolice/swagger-ui.html
```

---

## Environment variables (optional overrides)

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | `8082` | HTTP port |
| `SECURITY_JWT_SECRET` | *(see config)* | Base64-encoded HMAC-SHA512 key |
| `SECURITY_JWT_EXPIRATION_IN_MS` | `604800000` | Token TTL (7 days) |
| `HIBERNATE_DDL_AUTO` | `update` | Hibernate schema strategy |
| `JPA_SHOW_SQL` | `false` | Log SQL statements |
| `LOGGING_ROOT` | `INFO` | Root log level |

---

## Enable DEBUG logging at runtime

```bash
curl -X POST http://localhost:8082/sdss-service/actuator/loggers/com.mgdsw.sdss \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'
```

---

## Known local dev notes

- Oracle sorts `NULL` values first in `ORDER BY ... DESC` — any users with `NULL GRP_NUMBER` will cause a 500 on `/users/search`. Keep all users with valid `GRP_NUMBER` values.
- The `QUESTION` table (`Challenges` entity) has `MODERATOR` and `ASSISTANT_MODERATOR` FK columns pointing to `USERS.ID`. Deleting users without reassigning these FKs will break `/challenges/search`.
- The `ROLES_ENTITY` table (not `ROLES`) is what the application reads for role lookups.
