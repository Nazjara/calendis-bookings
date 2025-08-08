package com.nazjara.service;

import com.nazjara.config.BrowserHeadersHolder;
import com.nazjara.dto.AppointmentDto;
import com.nazjara.exception.AppointmentNotFoundException;
import com.nazjara.exception.BookingException;
import com.nazjara.model.request.ConfirmAppointmentRequest;
import com.nazjara.model.request.CreateAppointmentRequest;
import com.nazjara.model.response.AvailableSlotsResponse;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {

  private static final String GET_AVAILABLE_SLOTS_PATH = "/api/get_available_slots";
  private static final String CREATE_APPOINTMENT_PATH = "/api/appointment/";
  private static final String CONFIRM_PAGE_PATH = "/finalizeaza-programarea";
  private static final String APPOINTMENT_ID_SELECTOR = "input#appointment_group_id";

  private final RestClient restClient;
  private final AuthService authService;
  private final BrowserHeadersHolder headersHolder;

  @Value("${calendis.appointment.delete.only-me}")
  private int deleteOnlyMe;

  /**
   * Books an appointment by creating it and then confirming it in a single operation. This method
   * handles the complete booking flow: 1. Create the appointment 2. Extract the appointment ID from
   * the confirmation page 3. Confirm the appointment
   *
   * @param request the appointment creation request
   * @return the appointment details
   * @throws BookingException             if there is an error during the booking process
   * @throws AppointmentNotFoundException if the appointment ID cannot be found
   */
  public AppointmentDto bookAppointment(CreateAppointmentRequest request) {
    log.info("Booking appointment with complete flow: {}", request);

    var appointmentDto = createAppointment(request);
    log.info("Created appointment with ID: {}, now confirming", appointmentDto.getId());

    confirmAppointment(appointmentDto.getId());
    log.info("Successfully booked and confirmed appointment with ID: {}", appointmentDto.getId());

    return appointmentDto;
  }

  /**
   * Retrieves available slots for a specific service, location, and date from the Calendis API.
   *
   * @param serviceId  the ID of the service to check availability for
   * @param locationId the ID of the location to check availability for
   * @param date       the date (in Unix timestamp format) to check availability for
   * @param dayOnly    flag to indicate whether to return slots for the entire day (1) or not (0)
   * @return the available slots response
   * @throws BookingException if there is an error retrieving available slots
   */
  public AvailableSlotsResponse getAvailableSlots(int serviceId, int locationId, int stuffId,
      long date, int dayOnly) {
    var uriBuilder = UriComponentsBuilder.fromPath(GET_AVAILABLE_SLOTS_PATH)
        .queryParam("service_id", serviceId)
        .queryParam("location_id", locationId)
        .queryParam("date", date)
        .queryParam("day_only", dayOnly);

    if (stuffId != 0) {
      uriBuilder.queryParam("user_id", stuffId);
    }

    var uri = uriBuilder.build().toUriString();

    var response = restClient.get()
        .uri(uri)
        .headers(
            httpHeaders -> headersHolder.withCookie(buildCookieHeader()).forEach(httpHeaders::add))
        .retrieve()
        .body(AvailableSlotsResponse.class);

    log.info("Retrieved {} available slots",
        response.getAvailableSlots() != null ? response.getAvailableSlots().size() : 0);

    return response;
  }

  /**
   * Deletes an appointment for a given user via Calendis API.
   *
   * @param appointmentId the appointment ID to delete
   * @param userId        the end user ID on whose behalf the deletion is performed
   */
  public void deleteAppointment(long appointmentId, long userId) {
    var uri = UriComponentsBuilder.fromPath(CREATE_APPOINTMENT_PATH + appointmentId)
        .queryParam("only_me", deleteOnlyMe)
        .queryParam("end_user_id", userId)
        .build()
        .toUriString();

    restClient.delete()
        .uri(uri)
        .headers(
            httpHeaders -> headersHolder.withCookie(buildCookieHeader()).forEach(httpHeaders::add))
        .retrieve()
        .toBodilessEntity();

    log.info("Appointment {} deletion requested for user_id={} (only_me={})", appointmentId,
        userId, deleteOnlyMe);
  }

  private AppointmentDto createAppointment(CreateAppointmentRequest request) {
    restClient.post()
        .uri(CREATE_APPOINTMENT_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(
            httpHeaders -> headersHolder.withCookie(buildCookieHeader()).forEach(httpHeaders::add))
        .body(request)
        .retrieve()
        .toBodilessEntity();

    log.info("Appointment created successfully");

    var appointmentId = getAppointmentIdFromConfirmationPage();
    log.info("Extracted appointment ID: {}", appointmentId);

    return AppointmentDto.builder()
        .id(appointmentId)
        .dateUnix(System.currentTimeMillis() / 1000)
        .build();
  }

  private void confirmAppointment(long appointmentId) {
    var request = buildConfirmRequest(appointmentId);

    restClient.put()
        .uri(CREATE_APPOINTMENT_PATH + appointmentId)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(
            httpHeaders -> headersHolder.withCookie(buildCookieHeader()).forEach(httpHeaders::add))
        .body(request)
        .retrieve()
        .toBodilessEntity();

    log.info("Appointment {} confirmed successfully", appointmentId);
  }

  private long getAppointmentIdFromConfirmationPage() {
    var html = restClient.get()
        .uri(CONFIRM_PAGE_PATH)
        .headers(
            httpHeaders -> headersHolder.withCookie(buildCookieHeader()).forEach(httpHeaders::add))
        .retrieve()
        .body(String.class);

    return extractAppointmentId(html);
  }

  private long extractAppointmentId(String html) {
    try {
      var document = Jsoup.parse(html);
      var appointmentIdElement = document.select(APPOINTMENT_ID_SELECTOR).first();

      if (appointmentIdElement == null) {
        throw new AppointmentNotFoundException("Could not find appointment ID element in HTML");
      }

      var appointmentId = appointmentIdElement.attr("value");
      return Long.parseLong(appointmentId);
    } catch (Exception e) {
      log.error("Error extracting appointment ID from HTML", e);
      throw new BookingException("Error extracting appointment ID from HTML", e);
    }
  }

  private ConfirmAppointmentRequest buildConfirmRequest(long appointmentId) {
    var client = ConfirmAppointmentRequest.Client.builder()
        .ownAppointment(1)
        .dateUnix(System.currentTimeMillis() / 1000)
        .appointmentId(appointmentId)
        .build();

    return ConfirmAppointmentRequest.builder()
        .clients(Collections.singletonList(client))
        .build();
  }

  private String buildCookieHeader() {
    return "cookie_message=0; client_session=" + authService.getAuthDto().getClientSession();
  }
}