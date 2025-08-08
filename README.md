# Calendis Bookings

A Spring Boot application for making sport bookings through the Calendis API.

## Purpose

Calendis Bookings is designed to simplify the process of booking sports facilities through the Calendis platform. It provides both a REST API for manual bookings and an automated scheduler for recurring bookings. Key features include:

- Authentication with Calendis API
- Checking available slots for sports facilities
- Booking appointments in a single step
- Canceling appointments
- Automated scheduling of bookings at configured times

## Building and Running

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

### Build

```bash
mvn clean install
```

### Run

```bash
mvn spring-boot:run
```

Or with a specific profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Configuration

The application can be configured using environment variables. Below is a list of available environment variables organized by category.

### Setting Environment Variables

You can set environment variables in different ways:

```bash
# When running with Maven
export EMAIL_PRIMARY=your.email@example.com
mvn spring-boot:run

# Or in a single command
EMAIL_PRIMARY=your.email@example.com mvn spring-boot:run

# When running with Docker
docker run -e EMAIL_PRIMARY=your.email@example.com calendis-bookings
```

### API Configuration

| Environment Variable | Description | Default Value |
|----------------------|-------------|---------------|
| `CALENDIS_API_BASE_URL` | Base URL for the Calendis API | `https://www.calendis.ro` |

### User Credentials

These variables are required for authentication with the Calendis API:

| Environment Variable | Description | Default Value |
|----------------------|-------------|---------------|
| `EMAIL_PRIMARY` | Primary user email for authentication | (Required) |
| `EMAIL_SECONDARY` | Secondary user email for authentication | (Required) |
| `PASSWORD_PRIMARY` | Primary user password | (Required) |
| `PASSWORD_SECONDARY` | Secondary user password | (Required) |

### Location Configuration

| Environment Variable | Description | Default Value |
|----------------------|-------------|---------------|
| `LA_TERENURI_LOCATION_ID` | Location ID for La Terenuri facility | `4609` |
| `GHEORGHENI_LOCATION_ID` | Location ID for Gheorgheni facility | `165111` |

### Sport Service Configuration

#### La Terenuri

| Environment Variable | Description | Default Value |
|----------------------|-------------|---------------|
| `LA_TERENURI_TENIS_SERVICE_ID` | Service ID for tennis at La Terenuri | `37695` |
| `LA_TERENURI_SQUASH_SERVICE_ID` | Service ID for squash at La Terenuri | `37694` |
| `LA_TERENURI_TENIS_TABLE_SERVICE_ID` | Service ID for table tennis at La Terenuri | `37697` |
| `LA_TERENURI_TENIS_STUFF_ID` | Staff ID for tennis at La Terenuri (0 for any) | `0` |
| `LA_TERENURI_SQUASH_STUFF_ID` | Staff ID for squash at La Terenuri | `20359` |
| `LA_TERENURI_TENIS_TABLE_STUFF_ID` | Staff ID for table tennis at La Terenuri (0 for any) | `0` |

#### Gheorgheni

| Environment Variable | Description | Default Value |
|----------------------|-------------|---------------|
| `GHEORGHENI_TENIS_SERVICE_ID` | Service ID for tennis at Gheorgheni | `8029` |
| `GHEORGHENI_TENIS_TABLE_SERVICE_ID` | Service ID for table tennis at Gheorgheni | `8041` |
| `GHEORGHENI_TENIS_STUFF_ID` | Staff ID for tennis at Gheorgheni (0 for any) | `0` |
| `GHEORGHENI_TENIS_TABLE_STUFF_ID` | Staff ID for table tennis at Gheorgheni (0 for any) | `0` |

### Scheduling Configuration

The scheduler can be configured to automatically book appointments at specific times.

#### La Terenuri Tennis

| Environment Variable | Description | Default Value |
|----------------------|-------------|---------------|
| `LA_TERENURI_TENIS_PRIMARY_CRON_EXPRESSION` | When to run the primary scheduler (cron format) | `0 55 9 * * SAT` |
| `LA_TERENURI_TENIS_PRIMARY_ENABLED` | Whether primary schedule is enabled | `true` |
| `LA_TERENURI_TENIS_PRIMARY_APPOINTMENT_TIME` | Desired appointment time for primary schedule | `10:00` |
| `LA_TERENURI_TENIS_SECONDARY_CRON_EXPRESSION` | When to run the secondary scheduler | `0 55 10 * * SAT` |
| `LA_TERENURI_TENIS_SECONDARY_ENABLED` | Whether secondary schedule is enabled | `true` |
| `LA_TERENURI_TENIS_SECONDARY_APPOINTMENT_TIME` | Desired appointment time for secondary schedule | `11:00` |

#### La Terenuri Squash

| Environment Variable | Description | Default Value |
|----------------------|-------------|---------------|
| `LA_TERENURI_SQUASH_PRIMARY_CRON_EXPRESSION` | When to run the primary scheduler | `0 55 9 * * TUE` |
| `LA_TERENURI_SQUASH_PRIMARY_ENABLED` | Whether primary schedule is enabled | `true` |
| `LA_TERENURI_SQUASH_PRIMARY_APPOINTMENT_TIME` | Desired appointment time for primary schedule | `10:00` |
| `LA_TERENURI_SQUASH_SECONDARY_CRON_EXPRESSION` | When to run the secondary scheduler | `0 55 9 * * THU` |
| `LA_TERENURI_SQUASH_SECONDARY_ENABLED` | Whether secondary schedule is enabled | `true` |
| `LA_TERENURI_SQUASH_SECONDARY_APPOINTMENT_TIME` | Desired appointment time for secondary schedule | `10:00` |

Similar environment variables exist for table tennis at La Terenuri and all sports at Gheorgheni.

## How It Works

The application works by interfacing with the Calendis API:

1. **Authentication**: The app authenticates with Calendis using provided credentials to obtain a session token.

2. **Slot Availability**: It can check for available slots at specific facilities, locations, and dates.

3. **Booking Process**: When booking an appointment, the app handles the entire flow in one operation - creating the appointment, retrieving the confirmation details, and finalizing the booking.

4. **Canceling Appointments**: Users can cancel existing appointments by providing the appointment and user IDs.

5. **Automated Scheduling**: The scheduler component can automatically book appointments at configured times, useful for securing popular slots as soon as they become available.