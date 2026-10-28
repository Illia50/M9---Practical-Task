# Practical Task: Library Book Tracker API

Starter project for Data Architecture practical task: Build a REST API for managing books in a library using Spring Boot, Spring Data JPA and an H2 database.

## Task
Complete all of the 'TODO' portions of the code. The finished application should compile and all of the controller endpoints should be functional.

## Behavioural Requirements
- Books can be created
- All books can be retrieved
- A single book can be retrieved by ID
- Books can be deleted
- Books can be filtered and searched by their fields.
- Controller delegates to Service, which delegates to Repository
- DTOs are used for requests and responses

## Stretch goals
- Request validation
- Custom exception handling
- Searching by partial title
- Add 'borrow' and 'return' endpoints along with the new 'borrowedStatus' field to the Book entity

## M11: Spring Security

Branch `m11-spring-security` adds HTTP Basic authentication and role-based access to the API.

### Users (passwords are stored BCrypt-encoded in memory)

| Username | Password | Role  |
|----------|----------|-------|
| `user`   | `user`   | USER  |
| `admin`  | `admin`  | ADMIN |

### Access rules (`config/SecurityConfig`)

| Request                                  | No credentials | `user`          | `admin`         |
|------------------------------------------|----------------|-----------------|-----------------|
| Any `GET` endpoint                       | 401            | 200             | 200             |
| Any non-`GET` endpoint (`POST`, `PUT`, `DELETE`) | 401 | 403 | 2xx |

The API is stateless: credentials are checked on every request and no session cookie is created.

### Verifying in Postman

1. Run the app: `./mvnw spring-boot:run`
2. Import `postman/M11-Library-Security.postman_collection.json`.
3. Run the collection in order (Runner). Each request uses **Authorization → Basic Auth** with the user shown in its name, and its test checks the expected status code.

`SecurityConfigTests` checks the same rules automatically: `./mvnw test`.

### M9 review fixes included

- `BookNotFoundException` now extends `RuntimeException` and is mapped to `404 Not Found` by `GlobalExceptionHandler` (`@RestControllerAdvice`).
- Package `Exceptions` renamed to `exception`.
- Leftover `// TODO` comments removed; braces added to the one-line `if` in `deleteBook`.
