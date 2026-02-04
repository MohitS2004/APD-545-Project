# Blue Harbor Hotel Reservation Suite

Blue Harbor Hotel in Aurora Bay is adopting a full desktop reservation & billing suite to replace its manual workflows. The application follows a strict 3-tier architecture (Presentation with JavaFX kiosks/admin views, Application services with business rules & patterns, and Data tier backed by Spring Data JPA + Flyway migrations). Cross-cutting concerns—logging, security, configuration, and exporting—are applied consistently.

## Tech stack

- Java 21, JavaFX 21 (desktop-only, no web components)
- Spring Boot 3.3 (DI, validation, security), Spring Data JPA (Hibernate ORM)
- H2 (embedded dev DB) + PostgreSQL profile-ready, Flyway migrations
- PDFBox & OpenCSV for PDF/TXT/CSV exports
- SLF4J/Logback logging, BCrypt hashing, MapStruct-style DTO mappers (manual)

## Project layout

```
src/main/java/com/blueharbor/hotel
 ├─ app/ (JavaFX bootstrap + Spring context wiring)
 ├─ config/ (pricing, loyalty, occupancy policies, YAML driven)
 ├─ controller/ (JavaFX controllers for kiosk/admin/feedback/login)
 ├─ dto/ + viewmodel/ (UI-safe models)
 ├─ service/ (Reservation, Pricing, Loyalty, Payment, Report, Waitlist, Export, Auth, Notification)
 ├─ repository/ (Spring Data JPA repositories)
 ├─ model/ (entities & enums grouped by domain)
 ├─ strategy/ (pricing, discount, loyalty strategies)
 ├─ factory/ (room plan/add-on factories)
 ├─ observer/ (event publisher & listeners)
 ├─ util/, logging/, security/ (cross-cutting helpers)
src/main/resources
 ├─ view/ (FXML layouts; kiosk/admin/feedback/login)
 ├─ styles/ (CSS theme)
 ├─ config/ (YAML for pricing rules & loyalty caps)
 ├─ db/migration (Flyway SQL scripts)
 ├─ messages/ (i18n-ready resource bundles)
 └─ application.yaml (profiles for dev/prod)
```

## Running locally

1. Install JDK 21+ and Maven 3.9+.
2. From the project root, run:

```powershell
cd "c:\Users\solan\IdeaProjects\Projects\blueharbor-hotel"
mvn clean javafx:run
```

This builds the project and launches the JavaFX application using the configured launcher class.

## Main features

- Kiosk booking flow with date selection, occupancy rules, room types, add-ons, guest details, and review.
- Admin console for searching reservations, modifying/cancelling, recording payments and refunds, and processing checkout.
- Waitlist management with conversion to reservations and notifications.
- Dynamic pricing with weekday/weekend and seasonal multipliers defined in configuration.
- Payments with deposits, partial payments, and refunds as negative entries, updating totals and activity logs.
- Loyalty program with earning per paid amount, redemption with caps, and a ledger for audit.
- Tabular reporting for revenue, occupancy, feedback summary, and activity logs, with CSV/PDF/TXT export.
- Guest feedback screen tied to checked-out reservations with rating and comments.

## Notes

The detailed assignment documentation (architecture diagrams, entity lists, pattern usage, business rules, security/logging, reporting, and reflection) is provided separately in the required PDF/DOCX file (e.g., `ProjectDocumentation_<YourName>.pdf`).
3. `mvn javafx:run` (provided profile) or run `com.blueharbor.hotel.app.Launcher` from IntelliJ.
4. Log in via seeded admin accounts (`admin@blueharbor.com` / `Admin!234`, `manager@blueharbor.com` / `Manager!234`).
5. Use the kiosk tab to simulate walk-up bookings and the admin dashboard to manage reservations/payments/waitlist/feedback.

For PostgreSQL, set `SPRING_PROFILES_ACTIVE=prod` and update `application-prod.yaml` with your JDBC URL before launching.

## Notable patterns

- **Singleton**: `ConfigRegistry` exposes immutable pricing/loyalty/occupancy policies loaded from YAML.
- **Strategy**: Pricing (`PricingStrategy`, `WeekendMultiplierStrategy`, `SeasonalAdjustmentStrategy`), discount/loyalty redemption.
- **Factory**: `RoomPlanFactory` suggests room mixes based on adult/child counts and occupancy rules.
- **Decorator**: `AddOnDecorator` composes add-on services on top of a base `PricedComponent`.
- **Observer**: `ReservationEventPublisher` notifies waitlist + logging listeners on availability changes, payments, and checkouts.

## Reports & exports

- Revenue & occupancy tables (daily/weekly/monthly) with CSV/PDF buttons.
- Activity log tables with CSV/TXT export.
- Feedback dashboards with filters (rating, sentiment) and CSV export.

## Testing

- Service-layer tests focus on pricing, loyalty, discounts, and waitlist conversion logic using H2 + Testcontainers hooks.
- JavaFX controllers expose ViewModels that are unit-testable without UI thread coupling.

## Next steps

- Replace mock instructional GIF/video with final media assets.
- Integrate actual payment gateway/loyalty provider if required.
- Harden security policies (account lockout, audit streaming) for production environments.

## Reflection

Implementing the kiosk + admin desktop suite inside a single JavaFX/Spring Boot runtime kept wiring simple, but it also meant paying close attention to lazy-loading JPA collections and keeping the UI responsive while services run synchronously. The trickiest parts were translating the dense set of business rules (occupancy validation, loyalty redemption, tiered discounts) into small, testable services and keeping the UI flows “beginner friendly” without losing required functionality. Splitting responsibilities via strategies (pricing/loyalty), observers (waitlist notifications/activity log), and factories (room plan suggestions) helped contain that complexity. If this grew further, I’d invest in more explicit ViewModels per screen and add integration tests around the pricing + payment pipeline.
