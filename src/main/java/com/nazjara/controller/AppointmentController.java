package com.nazjara.controller;

import com.nazjara.dto.AppointmentDto;
import com.nazjara.model.request.CreateAppointmentRequest;
import com.nazjara.model.response.AvailableSlotsResponse;
import com.nazjara.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/appointment")
@RequiredArgsConstructor
public class AppointmentController {

  private final AppointmentService appointmentService;

  /**
   * Retrieves available slots for a specific service, location, and date.
   *
   * @param serviceId  the ID of the service to check availability for
   * @param locationId the ID of the location to check availability for
   * @param date       the date (in Unix timestamp format) to check availability for
   * @param dayOnly    flag to indicate whether to return slots for the entire day (1) or not (0)
   * @return a ResponseEntity with the available slots response
   */
  @GetMapping("/available-slots")
  public ResponseEntity<AvailableSlotsResponse> getAvailableSlots(
      @RequestParam("service_id") int serviceId,
      @RequestParam("location_id") int locationId,
      @RequestParam(value = "stuff_id", required = false) int stuffId,
      @RequestParam("date") long date,
      @RequestParam(value = "day_only", defaultValue = "1") int dayOnly) {

    log.info("Getting available slots for service: {}, location: {}, date: {}", serviceId,
        locationId, date);

    var response = appointmentService.getAvailableSlots(serviceId, locationId, stuffId, date,
        dayOnly);
    log.info("Successfully retrieved available slots");
    return ResponseEntity.ok(response);
  }

  /**
   * Books an appointment by creating it and then confirming it in a single operation. This endpoint
   * handles the complete booking flow: 1. Create the appointment 2. Extract the appointment ID from
   * the confirmation page 3. Confirm the appointment
   *
   * @param request the appointment creation request
   * @return a ResponseEntity containing the appointment details
   */
  @PostMapping("/book")
  public ResponseEntity<AppointmentDto> bookAppointment(
      @RequestBody CreateAppointmentRequest request) {
    log.info("Booking appointment with complete flow: {}", request);

    var appointmentDto = appointmentService.bookAppointment(request);
    log.info("Successfully booked and confirmed appointment with ID: {}", appointmentDto.getId());
    return ResponseEntity.ok(appointmentDto);
  }

  /**
   * Deletes an appointment by ID for the specified end user.
   *
   * @param id     the appointment ID to delete
   * @param userId the end user ID performing the deletion
   * @return 204 No Content on success
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAppointment(@PathVariable("id") long id,
      @RequestParam("user_id") long userId) {
    log.info("Deleting appointment id={} for user_id={}", id, userId);

    appointmentService.deleteAppointment(id, userId);
    log.info("Deletion requested for appointment id={} (end_user_id={})", id, userId);
    return ResponseEntity.noContent().build();
  }
}