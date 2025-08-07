# Calendis Bookings Application

A Spring Boot application for making sport bookings through the Calendis API.

## Features

- Login to Calendis API
- Get available slots for a specific service, location, and date
- Book appointments in a single step (create, extract ID, and confirm)
- Scheduled automatic appointment booking (configurable)

## Technologies Used

- Java 21
- Spring Boot 3.5.4
- Spring WebFlux (WebClient)
- Jsoup for HTML parsing
- Lombok for reducing boilerplate code
- JUnit and Mockito for testing

## API Endpoints

### Authentication

```
POST /api/auth/login
```

Request body:
```json
{
  "email": "your-email@example.com",
  "password": "your-password",
  "remember": true
}
```

Response:
```json
{
  "clientSession": "session-token"
}
```

### Appointments

#### Get Available Slots

```
GET /api/appointments/available-slots?service_id=37695&location_id=4609&date=1754678880&day_only=1
```

Response:
```json
{
  "message": "success",
  "success": 1,
  "available_slots": [
    {
      "is_available": 1,
      "staff_id": "20359",
      "time": 1754222400,
      "group_id": null
    }
  ],
  "service_details": null
}
```

#### Create Appointment

```
POST /api/appointments
```

Request body:
```json
{
  "appointments": [
    {
      "dateUnix": 1754730000,
      "dateUtcUnix": 1754719200,
      "location_id": 4609,
      "service_id": 37694,
      "staff_id": "20362",
      "startTime": "12:00",
      "originalSlot": 0
    }
  ],
  "group_id": null
}
```

Response:
```json
{
  "id": 5656048,
  "dateUnix": 1754730000
}
```

#### Book Appointment

Books an appointment by creating it and then confirming it in a single operation.

```
POST /api/appointments/book
```

Request body:
```json
{
  "appointments": [
    {
      "dateUnix": 1754730000,
      "dateUtcUnix": 1754719200,
      "location_id": 4609,
      "service_id": 37694,
      "staff_id": "20362",
      "startTime": "12:00",
      "originalSlot": 0
    }
  ],
  "group_id": null
}
```

Response:
```json
{
  "id": 5656048,
  "dateUnix": 1754730000
}
```

## Running the Example

The application includes an example class that demonstrates how to use the API. To run the example:

```
mvn spring-boot:run -Dspring-boot.run.profiles=example
```

Make sure to update the credentials in the `CalendisBookingExample` class before running the example.

## Configuration

The application can be configured through the `application.properties` file:

```properties
# Server configuration
server.port=8080

# Logging configuration
logging.level.root=INFO
logging.level.com.nazjara=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.springframework.web.reactive.function.client.ExchangeFunctions=DEBUG

# Calendis API configuration
calendis.api.base-url=https://www.calendis.ro

# Scheduler configuration
calendis.scheduler.cron-expression=0 0 8 * * ?
calendis.scheduler.enabled=false
calendis.scheduler.email=your-email@example.com
calendis.scheduler.password=your-password
calendis.scheduler.remember=true
calendis.scheduler.service-id=37695
calendis.scheduler.location-id=4609
calendis.scheduler.staff-id=20359
# calendis.scheduler.date=1754678880
calendis.scheduler.day-only=1
# calendis.scheduler.start-time=12:00
```

## Scheduled Appointment Booking

The application includes a scheduler that can automatically book appointments at configured times. This feature is useful for recurring bookings or when you want to ensure you get a slot as soon as they become available.

### How the Scheduler Works

1. The scheduler runs at the configured time (specified by the cron expression)
2. It logs in to the Calendis API using the configured credentials
3. It retrieves available slots based on the configured parameters
4. It selects an appropriate slot based on preferences (staff ID, start time)
5. It books the appointment using the selected slot

### Scheduler Configuration

The scheduler can be configured through the `application.properties` file:

| Property | Description | Default |
|----------|-------------|---------|
| `calendis.scheduler.cron-expression` | Cron expression for when the scheduler runs | `0 0 8 * * ?` (8:00 AM daily) |
| `calendis.scheduler.enabled` | Whether the scheduler is enabled | `false` |
| `calendis.scheduler.email` | Email for authentication | (required) |
| `calendis.scheduler.password` | Password for authentication | (required) |
| `calendis.scheduler.remember` | Whether to remember the login session | `true` |
| `calendis.scheduler.service-id` | Service ID for the appointment | (required) |
| `calendis.scheduler.location-id` | Location ID for the appointment | (required) |
| `calendis.scheduler.staff-id` | Staff ID for the appointment (optional) | (first available) |
| `calendis.scheduler.date` | Date for the appointment in Unix timestamp (optional) | (current date) |
| `calendis.scheduler.day-only` | Whether to return slots for the entire day | `1` |
| `calendis.scheduler.start-time` | Preferred start time in HH:mm format (optional) | (first available) |

### Enabling the Scheduler

To enable the scheduler, set `calendis.scheduler.enabled=true` in your `application.properties` file and provide the required configuration values.

## How It Works

1. **Authentication**: The application first authenticates with the Calendis API using the provided credentials. The API returns a session token that is used for subsequent requests.

2. **Getting Available Slots**: The application can retrieve available slots for a specific service, location, and date.

3. **Booking Appointments**: The application can book appointments in a single operation using the available slots. This process includes:
   - Creating the appointment
   - Automatically retrieving the confirmation page
   - Extracting the appointment ID using HTML parsing
   - Confirming the appointment

## HTML Parsing

The application uses Jsoup to parse the HTML response from the confirmation page. It extracts the appointment ID from the following HTML element:

```html
<input type="hidden" id="appointment_group_id" value="5656048"/>
```

This ID is then used to confirm the appointment.