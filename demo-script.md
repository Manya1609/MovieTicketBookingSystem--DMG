# Movie Ticket Booking System — Demo Script

Hi, I'm Manya, and in this video I'll walk you through my approach, low-level design, implementation, testing strategy, and a live demonstration of the Movie Ticket Booking System.

I started by carefully analyzing the problem statement and identifying the core business requirements.

The system needed to support:

- Multiple cities
- Multiple theatres per city
- Multiple screens per theatre
- Multiple shows per screen
- Seat-level booking
- Time-bound seat holds
- Pricing tiers
- Discount codes
- Refund policies
- Notifications
- Role-based access control
- Concurrency-safe seat allocation

Before jumping into implementation, I focused on understanding the domain and breaking the problem into smaller components.

I identified:

- Core functionalities
- User roles
- Domain entities
- Workflows
- State transitions
- APIs
- Design patterns
- Concurrency requirements

To streamline the development process, I created two reusable AI-assisted skills.

The first skill was focused on Low-Level Design generation and refinement.

Starting from the problem statement, it helped:

- Analyze requirements
- Identify entities and workflows
- Define APIs
- Review assumptions
- Suggest enhancements
- Generate the final implementation plan

The second skill was focused on implementation.

Once the design was finalized, this skill helped:

- Implement the approved design incrementally
- Follow the architecture and workflows
- Compile and validate the project
- Execute tests
- Update documentation
- Verify end-to-end workflows

Using this workflow, I first finalized the Low-Level Design, then implemented the system following the approved architecture, and finally validated the solution through unit tests, integration tests, and end-to-end testing.

Let me first walk through the final design.

---

## Low-Level Design Overview

[SHOW CLASS DIAGRAM]

The system revolves around two primary roles:

- Admin
- Customer

The domain hierarchy follows:

```
City
└── Theatre
    └── Screen
        └── Show
            └── ShowSeat
```

One important design decision was introducing **ShowSeat**.

Physical seats belong to a Screen, while seat availability belongs to a specific Show.

This allows the same physical seat to be:

- Booked for one show
- Available for another show

without affecting future shows.

The key business entities are:

User, City, Theatre, Screen, Seat, Movie, Show, ShowSeat, SeatHold, Booking, Payment, DiscountCode, PricingTier, RefundPolicy, Notification

For pricing and refunds, I used the **Strategy Pattern**.

This allows future extensions such as Festival pricing, Dynamic pricing, and Demand-based pricing without changing the booking flow.

For payments, I used a **Factory Pattern** that selects the appropriate payment gateway implementation based on the payment method.

For notifications, I used an **Event-Driven Observer-style** approach using Spring Events and Async Event Listeners.

This keeps notifications completely decoupled from the booking transaction.

---

## Booking Workflow

[SHOW END-TO-END WORKFLOW DIAGRAM]

Now let's look at the complete booking workflow.

The customer starts by selecting a movie and show.

The system displays seat availability.

When seats are selected:

- A Seat Hold is created.
- Seats move from `AVAILABLE` to `HELD` state.
- The hold remains valid for 10 minutes (configurable).

If payment succeeds:

- Seats become `BOOKED`.
- Booking is created.
- Payment is recorded.
- Notification event is published.

If payment fails:

- Seats are immediately released.
- Hold is marked expired.

To handle concurrent bookings, I used:

- Seat Hold mechanism
- Optimistic Locking using JPA `@Version`

This prevents double booking when multiple users attempt to reserve the same seat simultaneously.

A scheduler automatically releases expired holds and makes those seats available again.

Now let's move to the live implementation demo.

---

# Live Demo Workflow

Now that we've gone through the low-level design, class diagram, and booking workflow, let me demonstrate the actual implementation through Swagger UI.

---

## Prerequisites — Starting the Application

The system uses **PostgreSQL 16** as its database, managed via Docker Compose.

### Database Configuration

| Parameter | Value |
|---|---|
| Host | `localhost:5432` |
| Database | `moviedb` |
| Username | `postgres` |
| Password | `postgres` |

### Start the stack

```bash
docker-compose up -d
```

This starts:
- A `postgres:16-alpine` container on port `5432` with database `moviedb`
- The Spring Boot application on port `8080`

Wait for the health check to pass (the app container waits until PostgreSQL is ready), then open Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

or the API docs at:

```
http://localhost:8080/swagger.yaml
```

> **Database inspection:** connect directly with any PostgreSQL client (psql, DBeaver, TablePlus):
> ```
> psql -h localhost -U postgres -d moviedb
> ```
> JPA is configured with `ddl-auto=update` — all tables are created automatically on first startup.

---

I'll start with the **Admin workflow** because customers can only book movies and shows that have already been configured in the system.

---

## Admin Workflow

### Step 0: Login as Admin

A default admin account is seeded automatically on startup.

**API:** `POST /api/auth/login`

**Request body:**
```json
{
  "email": "admin@moviebooking.com",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "<JWT>",
  "email": "admin@moviebooking.com",
  "role": "ADMIN"
}
```

Copy the token value and click the **Authorize** button in Swagger UI. Enter `Bearer <token>` to authenticate all subsequent admin requests.

---

### Step 1: Create City

**API:** `POST /api/admin/cities`

**Request body:**
```json
{
  "name": "Mumbai"
}
```

**Response:** `{ "id": 1, "name": "Mumbai" }`

Note the `id` — this is the `cityId` used in the next step.

---

### Step 2: Create Theatre

**API:** `POST /api/admin/theatres`

**Request body:**
```json
{
  "name": "PVR Cinemas",
  "address": "Phoenix Mall, Lower Parel",
  "cityId": 1
}
```

**Response:** `{ "id": 1, "name": "PVR Cinemas", "cityId": 1, "cityName": "Mumbai" }`

Note the `id` — this is the `theatreId` used in the next step.

---

### Step 3: Create Screen

**API:** `POST /api/admin/screens`

**Request body:**
```json
{
  "name": "Screen 1",
  "totalSeats": 150,
  "theatreId": 1
}
```

**Response:** `{ "id": 1, "name": "Screen 1", "totalSeats": 150, "theatreId": 1, "theatreName": "PVR Cinemas" }`

Note the `id` — this is the `screenId` used in the seats and show steps.

---

### Step 4: Create Seat Layout

**API:** `POST /api/admin/screens/{screenId}/seats`

Path parameter: `screenId = 1`

**Request body:**
```json
{
  "seats": [
    { "rowNumber": 1, "seatNumber": 1, "seatType": "REGULAR" },
    { "rowNumber": 1, "seatNumber": 2, "seatType": "PREMIUM" }
  ]
}
```

Seats are assigned sequential IDs by the database (`id: 1`, `id: 2`).

`seatType` must be one of: `REGULAR`, `PREMIUM`

> Note: ShowSeat availability records (one per seat per show) are auto-created when a Show is scheduled on this screen — not at this step.

---

### Step 5: Create Movie

**API:** `POST /api/admin/movies`

**Request body:**
```json
{
  "title": "Interstellar",
  "description": "A team of explorers travel through a wormhole in space.",
  "duration": 169,
  "language": "English",
  "genre": "Sci-Fi"
}
```

`duration` is in minutes. `title` is the only required field.

**Response:** `{ "id": 1, "title": "Interstellar", ... }`

Note the `id` — this is the `movieId` used in the show step.

---

### Step 6: Schedule a Show

**API:** `POST /api/admin/shows`

**Request body:**
```json
{
  "movieId": 1,
  "screenId": 1,
  "startTime": "2026-07-15T18:00:00",
  "endTime": "2026-07-15T20:49:00"
}
```

`startTime` and `endTime` use ISO-8601 local date-time format.

The system validates that no other show on Screen 1 overlaps this time window.

**Response:** `{ "id": 1, "movieTitle": "Interstellar", "screenName": "Screen 1", "theatreName": "PVR Cinemas", "startTime": "2026-07-15T18:00:00", "endTime": "2026-07-15T20:49:00" }`

Note the show `id: 1`. At this point the system **automatically creates ShowSeat records** — one per physical seat on Screen 1 — each initialised to `AVAILABLE`.

---

### Step 7: Configure Pricing Tiers

**API:** `POST /api/admin/pricing-tiers` (call four times)

`seatType`: `REGULAR` or `PREMIUM` — `dayType`: `WEEKDAY` or `WEEKEND`

Effective price = `basePrice × multiplier`

**Tier 1 — Regular Weekday:**
```json
{ "seatType": "REGULAR", "dayType": "WEEKDAY", "basePrice": 200.00, "multiplier": 1.0 }
```
Effective price: ₹200.00

**Tier 2 — Regular Weekend:**
```json
{ "seatType": "REGULAR", "dayType": "WEEKEND", "basePrice": 200.00, "multiplier": 1.5 }
```
Effective price: ₹300.00

**Tier 3 — Premium Weekday:**
```json
{ "seatType": "PREMIUM", "dayType": "WEEKDAY", "basePrice": 350.00, "multiplier": 1.0 }
```
Effective price: ₹350.00

**Tier 4 — Premium Weekend:**
```json
{ "seatType": "PREMIUM", "dayType": "WEEKEND", "basePrice": 350.00, "multiplier": 1.5 }
```
Effective price: ₹525.00

All four pricing rules are now configured.

---

### Step 8: Configure Discount Code

**API:** `POST /api/admin/discount-codes`

**Request body:**
```json
{
  "code": "SUMMER20",
  "discountType": "FLAT",
  "discountValue": 50.00,
  "validFrom": "2026-06-01",
  "validTill": "2026-08-31",
  "maxUsage": 100
}
```

`discountType` must be `FLAT` (fixed amount off) or `PERCENTAGE` (percent of total).

Customers can now apply the code `SUMMER20` during booking to get ₹50 off.

---

### Step 9: Configure Refund Policies

**API:** `POST /api/admin/refund-policies` (call four times)

`minHoursBeforeShow` is the lower bound (inclusive) of the time band. The system applies the policy with the largest `minHoursBeforeShow` value that is ≤ hours remaining before show start.

**Slab 1 — More than 12 hours before show:**
```json
{ "minHoursBeforeShow": 12, "refundPercentage": 100 }
```

**Slab 2 — 4 to 12 hours before show:**
```json
{ "minHoursBeforeShow": 4, "refundPercentage": 50 }
```

**Slab 3 — 1 to 4 hours before show:**
```json
{ "minHoursBeforeShow": 1, "refundPercentage": 25 }
```

**Slab 4 — Less than 1 hour before show:**
```json
{ "minHoursBeforeShow": 0, "refundPercentage": 0 }
```

Refund policies are now active.

---

At this point the movie catalogue is fully configured. Now let's switch to the customer journey and perform an actual booking.

---

## Customer Workflow

### Step 1: Register Customer

**API:** `POST /api/auth/register`

**Request body:**
```json
{
  "name": "Manya",
  "email": "manya@example.com",
  "password": "secret123"
}
```

`password` must be at least 6 characters.

**Response:**
```json
{
  "token": "<JWT>",
  "email": "manya@example.com",
  "role": "CUSTOMER"
}
```

---

### Step 2: Login as Customer

**API:** `POST /api/auth/login`

**Request body:**
```json
{
  "email": "manya@example.com",
  "password": "secret123"
}
```

**Response:** JWT token with `"role": "CUSTOMER"`

Click **Authorize** in Swagger UI and replace the admin token with this customer token for all subsequent customer requests.

---

### Step 3: Browse Movies

**API:** `GET /api/movies`

No authentication required. This endpoint is public.

Execute the request.

We can now see the movie `Interstellar` (id: 1) that was created through the Admin workflow.

This demonstrates how the customer consumes the catalogue configured by the administrator.

---

### Step 4: Browse Shows by City

**API:** `GET /api/cities/{cityId}/shows`

Path parameter: `cityId = 1`

Execute the request.

The show for Interstellar on Screen 1 at PVR Cinemas (show id: 1) is now visible to the customer.

---

### Step 5: View Seat Availability

**API:** `GET /api/shows/{id}/seats`

Path parameter: `id = 1`

Execute the request.

The response is a list of ShowSeat records. All seats are currently `AVAILABLE`.

Note the `seatId` values in the response — these are the physical seat IDs used in the hold request:

```json
[
  { "id": 1, "seatId": 1, "rowNumber": 1, "seatNumber": 1, "seatType": "REGULAR", "status": "AVAILABLE" },
  { "id": 2, "seatId": 2, "rowNumber": 1, "seatNumber": 2, "seatType": "PREMIUM", "status": "AVAILABLE" }
]
```

The availability information comes from the ShowSeat entity, not the physical Seat entity.

---

### Step 6: Create Seat Hold

**API:** `POST /api/holds`

**Request body:**
```json
{
  "showId": 1,
  "seatIds": [1, 2]
}
```

`seatIds` are the **physical seat IDs** (the `seatId` field from the `GET /api/shows/{id}/seats` response above).

**Response:**
```json
{
  "id": 1,
  "showId": 1,
  "showSeatIds": [1, 2],
  "createdAt": "2026-07-15T17:30:00",
  "expiresAt": "2026-07-15T17:40:00"
}
```

The system creates a Seat Hold and returns the hold ID, the selected ShowSeat IDs, and the expiry time (10 minutes from now).

Note the hold `id: 1` — used in the booking step.

At this point the seats are **temporarily locked**. Other customers cannot reserve these seats while the hold is active.

---

### Step 7: Verify Seat Status Change

**API:** `GET /api/shows/{id}/seats`

Path parameter: `id = 1`

Execute the request again.

Notice that the selected seats are no longer `AVAILABLE`:

```json
[
  { "id": 1, "seatId": 1, "rowNumber": 1, "seatNumber": 1, "seatType": "REGULAR", "status": "HELD" },
  { "id": 2, "seatId": 2, "rowNumber": 1, "seatNumber": 2, "seatType": "PREMIUM", "status": "HELD" }
]
```

State transition: `AVAILABLE → HELD`

This demonstrates the seat locking mechanism.

---

### Step 8: Confirm Booking

**API:** `POST /api/bookings`

**Request body:**
```json
{
  "holdId": 1,
  "paymentMethod": "UPI",
  "discountCode": "SUMMER20"
}
```

`paymentMethod` must be one of: `UPI`, `CARD`, `WALLET`

`discountCode` is optional — omit the field to skip the discount.

Internally the system:

1. Validates hold ownership and expiry
2. Looks up pricing: REGULAR seat on a WEEKDAY show = ₹200, PREMIUM = ₹350 → subtotal ₹550
3. Applies FLAT discount code `SUMMER20` → -₹50 → total ₹500
4. Processes payment via the UPI gateway
5. Marks seats as `BOOKED`
6. Creates a `CONFIRMED` Booking record and a `SUCCESS` Payment record
7. Publishes a `BookingConfirmedEvent` (handled asynchronously — fires a Notification)

**Response:**
```json
{
  "id": 1,
  "showId": 1,
  "movieTitle": "Interstellar",
  "bookedSeats": [...],
  "totalAmount": 500.00,
  "bookingStatus": "CONFIRMED",
  "bookedAt": "2026-07-15T17:32:00"
}
```

The booking is now confirmed. Note the booking `id: 1` — used in the cancel step.

---

### Step 9: Verify Seat Status Again

**API:** `GET /api/shows/{id}/seats`

Path parameter: `id = 1`

Execute the request.

State transition: `HELD → BOOKED`

This demonstrates the complete booking lifecycle.

---

### Step 10: View Booking History

**API:** `GET /api/bookings/history`

No path parameters. Identity is resolved from the JWT token — each customer sees only their own bookings.

Execute the request.

The customer can now see booking id 1 with status `CONFIRMED`, movie `Interstellar`, total ₹500.00.

---

### Step 11: Cancel Booking

**API:** `PATCH /api/bookings/{id}/cancel`

Path parameter: `id = 1`

No request body required.

Internally the system:

1. Validates booking ownership (JWT must match the booking customer)
2. Checks booking is in `CONFIRMED` status
3. Verifies show has not yet started
4. Selects the matching refund policy time band based on hours remaining before show
5. Calculates refund amount
6. Releases booked seats back to `AVAILABLE`
7. Updates booking status
8. Publishes `BookingCancelledEvent` (and `RefundProcessedEvent` if refund > 0)

The booking is now cancelled.

---

### Step 12: Verify Seat Release

**API:** `GET /api/shows/{id}/seats`

Path parameter: `id = 1`

Execute the request.

State transition: `BOOKED → AVAILABLE`

This confirms that cancellation successfully released the seats back into inventory.

---

### Step 13: Verify Booking History

**API:** `GET /api/bookings/history`

Execute the request again.

The booking status is now updated to:

- `REFUNDED` — if cancelled more than 1 hour before the show (refund policy applied)
- `CANCELLED` — if cancelled less than 1 hour before the show (0% refund slab)

based on the configured refund policy.

---

## Database Verification

After running the full workflow, you can inspect the database state directly:

```bash
psql -h localhost -U postgres -d moviedb
```

Useful queries:

```sql
-- All bookings and their status
SELECT id, booking_status, total_amount, booked_at FROM bookings;

-- ShowSeat availability after cancel (should be AVAILABLE)
SELECT ss.id, s.row_number, s.seat_number, ss.status
FROM show_seats ss JOIN seats s ON ss.seat_id = s.id
WHERE ss.show_id = 1;

-- Notification events fired
SELECT id, type, status, created_at FROM notifications ORDER BY created_at;

-- Payment records
SELECT id, method, status, amount FROM payments;
```
