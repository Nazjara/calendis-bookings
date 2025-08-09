package com.nazjara.scheduler;

import com.nazjara.config.SchedulerConfig;
import com.nazjara.model.request.CreateAppointmentRequest;
import com.nazjara.model.request.LoginRequest;
import com.nazjara.model.response.AvailableSlotsResponse;
import com.nazjara.service.AppointmentService;
import com.nazjara.service.AuthService;
import com.nazjara.util.DateTimeUtils;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentScheduler {

  private static final String ROMANIA_TIMEZONE = "Europe/Bucharest";
  private static final String UTC_TIMEZONE = "UTC";
  private static final int BOOKING_ATTEMPTS_THRESHOLD = 5;

  private final AuthService authService;
  private final AppointmentService appointmentService;
  private final SchedulerConfig schedulerConfig;

  @Scheduled(cron = "${calendis.scheduler.la-terenuri.tenis.primary.cron-expression}", zone = ROMANIA_TIMEZONE)
  @Async
  public void scheduleLaTerenuriTenisPrimary() {
    if (!schedulerConfig.getLaTerenuri().getTenis().getPrimary().isEnabled()) {
      log.info("La Terenuri Tenis Primary scheduler is disabled. Skipping scheduled run.");
      return;
    }

    scheduleAppointmentBooking(
        schedulerConfig.getEmail().getPrimary(),
        schedulerConfig.getPassword().getPrimary(),
        schedulerConfig.isRemember(),
        schedulerConfig.getLaTerenuri().getTenis().getServiceId(),
        schedulerConfig.getLaTerenuri().getLocationId(),
        schedulerConfig.getLaTerenuri().getTenis().getStuffId(),
        schedulerConfig.getLaTerenuri().getTenis().getPrimary().getAppointmentTime(),
        schedulerConfig.getDayOnly(), 1);
  }

  @Scheduled(cron = "${calendis.scheduler.la-terenuri.tenis.secondary.cron-expression}", zone = ROMANIA_TIMEZONE)
  @Async
  public void scheduleLaTerenuriTenisSecondary() {
    if (!schedulerConfig.getLaTerenuri().getTenis().getSecondary().isEnabled()) {
      log.info("La Terenuri Tenis Secondary scheduler is disabled. Skipping scheduled run.");
      return;
    }

    scheduleAppointmentBooking(
        schedulerConfig.getEmail().getSecondary(),
        schedulerConfig.getPassword().getSecondary(),
        schedulerConfig.isRemember(),
        schedulerConfig.getLaTerenuri().getTenis().getServiceId(),
        schedulerConfig.getLaTerenuri().getLocationId(),
        schedulerConfig.getLaTerenuri().getTenis().getStuffId(),
        schedulerConfig.getLaTerenuri().getTenis().getSecondary().getAppointmentTime(),
        schedulerConfig.getDayOnly(), 1);
  }

  @Scheduled(cron = "${calendis.scheduler.la-terenuri.squash.primary.cron-expression}", zone = ROMANIA_TIMEZONE)
  @Async
  public void scheduleLaTerenuriSquashPrimary() {
    if (!schedulerConfig.getLaTerenuri().getSquash().getPrimary().isEnabled()) {
      log.info("La Terenuri Squash Primary scheduler is disabled. Skipping scheduled run.");
      return;
    }

    scheduleAppointmentBooking(
        schedulerConfig.getEmail().getPrimary(),
        schedulerConfig.getPassword().getPrimary(),
        schedulerConfig.isRemember(),
        schedulerConfig.getLaTerenuri().getSquash().getServiceId(),
        schedulerConfig.getLaTerenuri().getLocationId(),
        schedulerConfig.getLaTerenuri().getSquash().getStuffId(),
        schedulerConfig.getLaTerenuri().getSquash().getPrimary().getAppointmentTime(),
        schedulerConfig.getDayOnly(), 1);
  }

  @Scheduled(cron = "${calendis.scheduler.la-terenuri.squash.secondary.cron-expression}", zone = ROMANIA_TIMEZONE)
  @Async
  public void scheduleLaTerenuriSquashSecondary() {
    if (!schedulerConfig.getLaTerenuri().getSquash().getSecondary().isEnabled()) {
      log.info("La Terenuri Squash Secondary scheduler is disabled. Skipping scheduled run.");
      return;
    }

    scheduleAppointmentBooking(
        schedulerConfig.getEmail().getSecondary(),
        schedulerConfig.getPassword().getSecondary(),
        schedulerConfig.isRemember(),
        schedulerConfig.getLaTerenuri().getSquash().getServiceId(),
        schedulerConfig.getLaTerenuri().getLocationId(),
        schedulerConfig.getLaTerenuri().getSquash().getStuffId(),
        schedulerConfig.getLaTerenuri().getSquash().getSecondary().getAppointmentTime(),
        schedulerConfig.getDayOnly(), 1);
  }

  @Scheduled(cron = "${calendis.scheduler.la-terenuri.tenis-table.primary.cron-expression}", zone = ROMANIA_TIMEZONE)
  @Async
  public void scheduleLaTerenuriTenisTablePrimary() {
    if (!schedulerConfig.getLaTerenuri().getTenisTable().getPrimary().isEnabled()) {
      log.info("La Terenuri Tenis Table Primary scheduler is disabled. Skipping scheduled run.");
      return;
    }

    scheduleAppointmentBooking(
        schedulerConfig.getEmail().getPrimary(),
        schedulerConfig.getPassword().getPrimary(),
        schedulerConfig.isRemember(),
        schedulerConfig.getLaTerenuri().getTenisTable().getServiceId(),
        schedulerConfig.getLaTerenuri().getLocationId(),
        schedulerConfig.getLaTerenuri().getTenisTable().getStuffId(),
        schedulerConfig.getLaTerenuri().getTenisTable().getPrimary().getAppointmentTime(),
        schedulerConfig.getDayOnly(), 1);
  }

  @Scheduled(cron = "${calendis.scheduler.gheorgheni.tenis.primary.cron-expression}", zone = ROMANIA_TIMEZONE)
  @Async
  public void scheduleGheorgheniTenisPrimary() {
    if (!schedulerConfig.getGheorgheni().getTenis().getPrimary().isEnabled()) {
      log.info("Gheorgheni Tenis Primary scheduler is disabled. Skipping scheduled run.");
      return;
    }

    scheduleAppointmentBooking(
        schedulerConfig.getEmail().getPrimary(),
        schedulerConfig.getPassword().getPrimary(),
        schedulerConfig.isRemember(),
        schedulerConfig.getGheorgheni().getTenis().getServiceId(),
        schedulerConfig.getGheorgheni().getLocationId(),
        schedulerConfig.getGheorgheni().getTenis().getStuffId(),
        schedulerConfig.getGheorgheni().getTenis().getPrimary().getAppointmentTime(),
        schedulerConfig.getDayOnly(), 1);
  }

  @Scheduled(cron = "${calendis.scheduler.gheorgheni.tenis.secondary.cron-expression}", zone = ROMANIA_TIMEZONE)
  @Async
  public void scheduleGheorgheniTenisSecondary() {
    if (!schedulerConfig.getGheorgheni().getTenis().getSecondary().isEnabled()) {
      log.info("Gheorgheni Tenis Secondary scheduler is disabled. Skipping scheduled run.");
      return;
    }

    scheduleAppointmentBooking(
        schedulerConfig.getEmail().getSecondary(),
        schedulerConfig.getPassword().getSecondary(),
        schedulerConfig.isRemember(),
        schedulerConfig.getGheorgheni().getTenis().getServiceId(),
        schedulerConfig.getGheorgheni().getLocationId(),
        schedulerConfig.getGheorgheni().getTenis().getStuffId(),
        schedulerConfig.getGheorgheni().getTenis().getSecondary().getAppointmentTime(),
        schedulerConfig.getDayOnly(), 1);
  }

  @Scheduled(cron = "${calendis.scheduler.gheorgheni.tenis-table.primary.cron-expression}", zone = ROMANIA_TIMEZONE)
  @Async
  public void scheduleGheorgheniTenisTablePrimary() {
    if (!schedulerConfig.getGheorgheni().getTenisTable().getPrimary().isEnabled()) {
      log.info("Gheorgheni Tenis Table Primary scheduler is disabled. Skipping scheduled run.");
      return;
    }

    scheduleAppointmentBooking(
        schedulerConfig.getEmail().getPrimary(),
        schedulerConfig.getPassword().getPrimary(),
        schedulerConfig.isRemember(),
        schedulerConfig.getGheorgheni().getTenisTable().getServiceId(),
        schedulerConfig.getGheorgheni().getLocationId(),
        schedulerConfig.getGheorgheni().getTenisTable().getStuffId(),
        schedulerConfig.getGheorgheni().getTenisTable().getPrimary().getAppointmentTime(),
        schedulerConfig.getDayOnly(), 1);
  }

  private void scheduleAppointmentBooking(String email, String password, boolean remember,
      int serviceId, int locationId, int stuffId, String appointmentTime, int dayOnly,
      int attempt) {
    log.info("Starting scheduled appointment booking...");

    try {
      login(email, password, remember);

      AvailableSlotsResponse.Slot selectedSlot = null;
      Instant firstThresholdTime = Instant.now().plusSeconds(60);
      Instant secondThresholdTime = Instant.now().plus(1, ChronoUnit.HOURS);

      while (true) {
        if (Instant.now().isAfter(secondThresholdTime)) {
          log.info("Slot was not found within the threshold time. Stopping search.");
          break;
        }

        selectedSlot = findSlot(appointmentTime,
            getAvailableSlots(serviceId, locationId, stuffId, dayOnly));

        if (selectedSlot != null) {
          log.info("Found available slot: {}", selectedSlot);
          break;
        }

        Thread.sleep(Instant.now().isAfter(firstThresholdTime) ? 60000 : 1000);
      }

      if (selectedSlot == null) {
        log.info(
            "No available slot found for the specified appointment time: {}. Stopping the job...",
            appointmentTime);
        return;
      }

      log.info("Selected slot: {}", selectedSlot);
      bookAppointment(selectedSlot, locationId, serviceId, appointmentTime);
      log.info("Scheduled appointment booking completed successfully.");
    } catch (Exception e) {
      log.error("Error during scheduled appointment booking. Attempting a retry...", e);

      if (e.getMessage() != null && e.getMessage().contains("once_per_week")) {
        log.info("Appointment already booked. Stopping further attempts.");
        return;
      }

      if (attempt >= BOOKING_ATTEMPTS_THRESHOLD) {
        log.warn("Maximum booking attempts reached. Stopping further attempts.");
        return;
      }

      try {
        Thread.sleep(attempt * 1000L);
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
        log.info("Job interrupted, stopping gracefully.");
        return;
      }

      scheduleAppointmentBooking(email, password, remember, serviceId, locationId, stuffId,
          appointmentTime, dayOnly, ++attempt);
    }
  }

  private void login(String email, String password, boolean remember) {
    log.info("Logging in with email: {}", email);

    authService.login(LoginRequest.builder()
        .email(email)
        .password(password)
        .remember(remember)
        .build());

    log.info("Login successful");
  }

  private List<AvailableSlotsResponse.Slot> getAvailableSlots(int serviceId, int locationId,
      int stuffId, int dayOnly) {
    log.info("Getting available slots for service: {}, location: {}", serviceId, locationId);

    var response = appointmentService.getAvailableSlots(serviceId, locationId, stuffId,
        DateTimeUtils.getFutureTimestamp(ROMANIA_TIMEZONE, 14, "14:00"), dayOnly);

    if (response == null || response.getAvailableSlots() == null) {
      log.info("No response or no available slots returned from the API");
      return Collections.emptyList();
    }

    var availableSlots = response.getAvailableSlots().stream()
        .filter(slot -> slot.getIsAvailable() == 1)
        .toList();

    log.info("Found {} available slots", availableSlots.size());
    return availableSlots;
  }

  private AvailableSlotsResponse.Slot findSlot(String appointmentTime,
      List<AvailableSlotsResponse.Slot> availableSlots) {
    return availableSlots.stream().filter(
            slot -> slot.getTime() == DateTimeUtils.getFutureTimestamp(ROMANIA_TIMEZONE, 14,
                appointmentTime))
        .findFirst()
        .orElse(null);
  }

  private void bookAppointment(AvailableSlotsResponse.Slot slot, int locationId, int serviceId,
      String appointmentTime) {
    log.info("Booking appointment for slot: {}", slot);

    var appointment = CreateAppointmentRequest.Appointment.builder()
        .dateUnix(slot.getTime())
        .dateUtcUnix(DateTimeUtils.convertTimestamp(slot.getTime(), ROMANIA_TIMEZONE, UTC_TIMEZONE))
        .locationId(locationId)
        .serviceId(serviceId)
        .staffId(slot.getStaffId())
        .startTime(appointmentTime)
        .originalSlot(0)
        .build();

    var request = CreateAppointmentRequest.builder()
        .appointments(Collections.singletonList(appointment))
        .groupId(slot.getGroupId())
        .build();

    try {
      var appointmentDto = appointmentService.bookAppointment(request);
      log.info("Successfully booked appointment with ID: {}", appointmentDto.getId());
    } catch (Exception e) {
      log.error("Failed to book appointment", e);
      throw e;
    }
  }
}