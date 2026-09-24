# Movie Booking System — Implementation Plan

## Build Order Rationale

Dependencies flow in one direction:

```
Enums → Entities → Repositories → Patterns (pricing/payment/refund)
     → Services → Controllers → Cross-cutting (auth, events, scheduler)
```

Entities cannot be built before their enum types. Repositories cannot be built before their entity. Services depend on repositories and pattern classes. Controllers depend on services. Auth and events are cross-cutting but are introduced early so services can use them from the start.

---

## Project / Package Layout

```
com.moviebooking
├── config/           AsyncConfig, SecurityConfig
├── controller/       AdminController, AuthController, BookingController,
│                     MovieController, SeatHoldController, ShowController
├── dto/
│   ├── request/      CreateCityRequest, CreateTheatreRequest, CreateScreenRequest,
│   │                 BulkCreateSeatsRequest, CreateMovieRequest, CreateShowRequest,
│   │                 CreateSeatHoldRequest, CreateBookingRequest, CreatePricingTierRequest,
│   │                 CreateDiscountCodeRequest, CreateRefundPolicyRequest,
│   │                 LoginRequest, RegisterRequest
│   └── response/     CityResponse, TheatreResponse, ScreenResponse, MovieResponse,
│                     ShowResponse, ShowSeatResponse, SeatHoldResponse, BookingResponse,
│                     PricingTierResponse, DiscountCodeResponse, RefundPolicyResponse,
│                     AuthResponse, ErrorResponse
├── entity/           City, Theatre, Screen, Seat, Movie, Show, ShowSeat,
│                     SeatHold, Booking, Payment, DiscountCode, PricingTier,
│                     RefundPolicy, Notification, User
├── enums/            Role, SeatType, DayType, SeatStatus, BookingStatus,
│                     PaymentStatus, PaymentMethod, DiscountType,
│                     NotificationType, NotificationStatus
├── event/            BookingConfirmedEvent, BookingCancelledEvent, RefundProcessedEvent
├── exception/        BusinessException, ResourceNotFoundException, GlobalExceptionHandler
├── listener/         NotificationEventListener
├── payment/          PaymentGateway (interface), PaymentGatewayFactory,
│                     UpiPaymentGateway, CardPaymentGateway, WalletPaymentGateway,
│                     PaymentResult
├── pricing/          PricingStrategy (interface), PricingContext,
│                     RegularWeekdayPricingStrategy, RegularWeekendPricingStrategy,
│                     PremiumWeekdayPricingStrategy, PremiumWeekendPricingStrategy
├── refund/           RefundStrategy (interface), TimeBasedRefundStrategy
├── repository/       UserRepository, CityRepository, TheatreRepository, ScreenRepository,
│                     SeatRepository, MovieRepository, ShowRepository, ShowSeatRepository,
│                     SeatHoldRepository, BookingRepository, PaymentRepository,
│                     DiscountCodeRepository, PricingTierRepository,
│                     RefundPolicyRepository, NotificationRepository
├── scheduler/        SeatHoldExpiryScheduler
├── security/         JwtUtil (or equivalent), JwtFilter, UserDetailsServiceImpl
└── service/          AuthService, AdminService, MovieService, ShowService,
                      SeatHoldService, BookingService
```

---

## Milestones

### Milestone 1 — Foundation: Enums, Entities, Repositories

**Goal:** All domain types and JPA-managed tables exist; the application starts and H2 console shows the schema.

**Depends on:** nothing

- [x] Define all 10 enums (`Role`, `SeatType`, `DayType`, `SeatStatus`, `BookingStatus`, `PaymentStatus`, `PaymentMethod`, `DiscountType`, `NotificationType`, `NotificationStatus`) — *Done when: enums compile with no warnings*
- [x] Implement 15 entities with correct field types, `@Column` constraints, `@ManyToOne`/`@OneToMany` relationships, `@Version` on `ShowSeat`, and N:M join tables (`seat_hold_show_seats`, `booking_show_seats`) — *Done when: application starts and H2 shows all tables*
- [x] Define JPA `@Repository` interfaces (one per entity; custom finders where needed, e.g. `ShowSeatRepository.findByShowIdAndSeatIdIn`, `SeatHoldRepository.findExpiredHolds`, `RefundPolicyRepository.findBestMatchingPolicy`) — *Done when: repository beans are injected without errors*
- [x] Add `application.properties` with H2, JPA (`ddl-auto=create-drop`, `open-in-view=false`), JWT keys, and `app.hold.duration.minutes=10` — *Done when: app starts cleanly*

**Tests:** Unit tests verifying entity field mappings; integration test confirming H2 schema matches entity definitions.

---

### Milestone 2 — Auth & Security

**Goal:** Registration, login, and role-based endpoint protection work end-to-end.

**Depends on:** Milestone 1 (`User` entity, `UserRepository`)

- [x] Implement `AuthService.register` (BCrypt password, save User with role=CUSTOMER, return JWT) and `AuthService.login` (authenticate, return JWT)
- [x] Implement `JwtUtil` (sign, validate, extract claims) and `JwtFilter` (parse Bearer token, set SecurityContext)
- [x] Configure `SecurityConfig`: public endpoints (`/api/auth/**`, browse), `ROLE_CUSTOMER` endpoints, `ROLE_ADMIN` endpoints; disable CSRF; stateless session
- [x] Implement `AuthController` (`POST /api/auth/register`, `POST /api/auth/login`)
- [x] Implement `GlobalExceptionHandler` (400, 401, 403, 404, 409, 500) returning `{ status, message }`

*Done when: `POST /api/auth/register` returns a JWT; an unauthenticated request to a protected endpoint returns 401.*

**Tests:** Unit test JWT sign/validate; integration tests for register, login, and role-based 403.

---

### Milestone 3 — Admin Catalogue CRUD

**Goal:** Admin can create and manage all catalogue entities.

**Depends on:** Milestones 1–2

- [x] Implement `AdminService` with CRUD for: City, Theatre, Screen (+ bulk seat creation), Movie, Show, PricingTier, DiscountCode, RefundPolicy
- [x] Implement `AdminController` mapping each operation to its `/api/admin/**` endpoint
- [x] Add request DTOs with `@Valid` annotations and response DTOs

*Done when: Admin can create a City → Theatre → Screen → Seats → Movie → Show → PricingTier → DiscountCode → RefundPolicy via the API.*

**Tests:** Integration tests for each CRUD operation; test cascade delete (delete City cascades to Theatres and Screens).

---

### Milestone 4 — Browse & Seat Availability

**Goal:** Customers can discover movies, shows, and seat availability.

**Depends on:** Milestones 1–3

- [x] Implement `MovieService.listMovies`, `MovieService.getMovie`
- [x] Implement `ShowService.listShowsByCity`, `ShowService.getShow`, `ShowService.getShowSeats`
- [x] Implement `MovieController`, `ShowController`
- [x] Verify ShowSeats are created for each Show (either auto-created on show creation in AdminService, or via a separate step)

*Done when: `GET /api/cities/{id}/shows` returns shows with seat availability counts.*

**Tests:** Integration tests covering browse endpoints; verify ShowSeat rows exist after show creation.

---

### Milestone 5 — Seat Hold & Expiry

**Goal:** Customers can hold seats; holds expire automatically; concurrent hold attempts are conflict-safe.

**Depends on:** Milestones 1–4

- [x] Implement `SeatHoldService.createHold`: validate AVAILABLE status, set HELD, create SeatHold
- [x] Implement `SeatHoldController` (`POST /api/holds`)
- [x] Implement `SeatHoldExpiryScheduler` (`@Scheduled fixedDelay=30000`): query expired non-expired holds, reset HELD ShowSeats to AVAILABLE, mark holds expired
- [x] Enable `@EnableScheduling` in application config

*Done when: a hold is created; a concurrent hold on the same seat returns 409; after hold TTL the scheduler releases the seats.*

**Tests:** Unit test scheduler logic; integration test concurrent hold attempts (verify 409 on second request); test hold expiry flow.

---

### Milestone 6 — Pricing & Booking

**Goal:** Customers can confirm a hold into a booking with correct pricing and optional discount.

**Depends on:** Milestones 1–5

- [x] Implement `PricingStrategy` interface and four concrete strategies (REGULAR×WEEKDAY, REGULAR×WEEKEND, PREMIUM×WEEKDAY, PREMIUM×WEEKEND); each computes `basePrice × BigDecimal.valueOf(multiplier)`
- [x] Implement `PricingContext` (dispatches to the matching strategy by SeatType × DayType)
- [x] Implement `PaymentGateway` interface and three mock gateways (Upi, Card, Wallet); implement `PaymentGatewayFactory`
- [x] Implement `BookingService.createBooking`: validate hold, compute price, apply discount, process payment; **on payment failure: reset ShowSeats to AVAILABLE, mark hold expired, throw BusinessException**; on success: create Booking (CONFIRMED) + Payment (SUCCESS), expire hold, publish `BookingConfirmedEvent`
- [x] Implement `BookingController` (`POST /api/bookings`)

*Done when: a confirmed booking returns the correct total amount; a failed payment immediately releases the held seats.*

**Tests:** Unit tests for each PricingStrategy; integration tests covering: correct price calculation, discount application (FLAT and PERCENTAGE), payment failure seat release, optimistic lock on concurrent bookings.

---

### Milestone 7 — Cancellation & Refunds

**Goal:** Customers can cancel confirmed bookings with configurable time-band refunds.

**Depends on:** Milestone 6

- [x] Implement `RefundStrategy` interface and `TimeBasedRefundStrategy` (`originalAmount × refundPercentage / 100`, HALF_UP)
- [x] Implement `BookingService.cancelBooking`: validate ownership + CONFIRMED status + show not started; query best-matching RefundPolicy; compute refund; release seats; set status REFUNDED or CANCELLED; publish events
- [x] Implement `BookingController` (`PATCH /api/bookings/{id}/cancel`, `GET /api/bookings/history`)

*Done when: cancellation >12 h before show returns 100% refund; cancellation after show start returns 400.*

**Tests:** Unit test `TimeBasedRefundStrategy` for each time band including edge cases; integration tests for all refund bands and post-show-start guard.

---

### Milestone 8 — Async Notifications

**Goal:** Booking events trigger notification records asynchronously without blocking the booking transaction.

**Depends on:** Milestones 6–7

- [x] Define `BookingConfirmedEvent`, `BookingCancelledEvent`, `RefundProcessedEvent`
- [x] Implement `NotificationEventListener`: `@Async + @TransactionalEventListener(AFTER_COMMIT) + @Transactional(REQUIRES_NEW)` handlers for each event type; create `Notification` with status=SENT
- [x] Configure `AsyncConfig` (Spring `@Async` executor)

*Done when: after a successful booking, a Notification row exists in the DB with type=BOOKING_CONFIRMED and status=SENT, without the booking API call waiting for it.*

**Tests:** Integration test verifying Notification row is created after booking commit; verify notifications do not block or roll back booking on listener failure.

---

### Milestone 9 — Hardening (Open Enhancements)

**Goal:** Address the correctness gaps identified in the design review.

**Depends on:** Milestones 1–8

- [ ] **O2 — DiscountCode race condition:** Add `@Version` to `DiscountCode` OR replace the read-increment pattern with a conditional JPQL update. — *Done when: concurrent bookings with the same code cannot exceed maxUsage*
- [ ] **O1 — Production database:** Confirm target DB (PostgreSQL recommended). Update `spring.datasource.*`, set `ddl-auto=validate`, introduce Flyway with an initial migration script generated from the H2 schema. — *Done when: application starts against PostgreSQL with Flyway baseline applied*
- [ ] **O5 — JWT secret:** Move `app.jwt.secret` to an environment variable or secrets manager; remove hard-coded value from `application.properties`. — *Done when: application starts without a secret in the properties file*
- [ ] **O6 — Show overlap enforcement:** Add a guard in `AdminService.createShow` querying for overlapping shows on the same screen. — *Done when: creating two overlapping shows on the same screen returns 400*
- [ ] **F5 — Pagination:** Add `Pageable` to `GET /api/movies`, `GET /api/cities/{id}/shows`, `GET /api/bookings/history`. — *Done when: `?page=0&size=10` returns paginated results*

---

## Cross-Cutting Work

| Concern | Introduced | Notes |
|---|---|---|
| Error handling | Milestone 2 | `GlobalExceptionHandler` covers all milestones |
| `@Valid` input validation | Milestone 3 | All request DTOs use Bean Validation annotations |
| JWT auth + Spring Security | Milestone 2 | All subsequent endpoints inherit security config |
| Optimistic locking | Milestone 5 | `@Version` on ShowSeat; 409 wired in GlobalExceptionHandler |
| Async executor | Milestone 8 | `AsyncConfig`; required before `@Async` listeners work |
| Scheduling | Milestone 5 | `@EnableScheduling`; required before `@Scheduled` fires |
| Logging | Throughout | SLF4J; key events: hold expiry count, notification sent, booking conflicts |

---

## Testing Strategy

| Area | Test type | Risk level |
|---|---|---|
| Pricing strategies | Unit | Medium — float precision edge cases |
| Refund time-band boundaries | Unit | High — off-by-one on hour boundaries |
| Concurrent seat hold | Integration | High — requires real DB transaction; mock won't catch it |
| Booking transaction atomicity | Integration | High — payment fail must not leave partial state |
| Discount concurrent usage | Integration | High — race condition requires concurrent threads |
| Hold expiry scheduler | Integration | Medium — use `@SpyBean` to trigger manually |
| JWT auth & roles | Integration | Low — well-covered by Spring Security tests |
| Admin CRUD cascades | Integration | Medium — delete city should cascade |

Run all tests:
```bash
mvn test
```
Run a single class:
```bash
mvn test -Dtest=BookingServiceTest
```
Run a single method:
```bash
mvn test -Dtest=BookingServiceTest#shouldReleaseSeatOnPaymentFailure
```

---

## Configuration & Environment

**Development (current):**
```properties
spring.datasource.url=jdbc:h2:mem:moviedb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
app.hold.duration.minutes=10
app.jwt.secret=<base64-encoded-secret>
app.jwt.expiration=86400000
```

**Production (target — pending O1):**
```properties
spring.datasource.url=jdbc:postgresql://host:5432/moviedb
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
spring.h2.console.enabled=false
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration=86400000
```

**Run commands:**
```bash
mvn clean install          # build + all tests
mvn spring-boot:run        # start on port 8080
mvn test                   # run tests only
```

H2 console (dev): `http://localhost:8080/h2-console`
JDBC URL: `jdbc:h2:mem:moviedb`, user: `sa`, no password.

---

## Sequencing Notes & Risks

| Note | Detail |
|---|---|
| Milestones 1–5 are strictly sequential | Each depends on the previous |
| Milestones 6 and 7 can be done in parallel after M5 | Pricing/booking and cancellation/refund are independent code paths |
| Milestone 8 (notifications) is independent after M6–M7 | Events are published regardless; listener can be added last |
| Milestone 9 items are independent of each other | Each enhancement is a self-contained change |
| Riskiest step: concurrent seat hold (M5) | Must use a real DB transaction; H2 works for dev, but test with PostgreSQL before going to production |
| Second riskiest: DiscountCode race (M9/O2) | Easy to miss in single-threaded tests; write a concurrent integration test |
| Flyway baseline (M9/O1) | Generate the initial migration from the H2 schema (`spring.jpa.generate-ddl=true` in a throwaway run) then validate against PostgreSQL |
