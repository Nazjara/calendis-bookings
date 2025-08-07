package com.nazjara.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmAppointmentRequest {

  private List<Client> clients;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Client {

    @JsonProperty("own_appointment")
    private int ownAppointment;

    private long dateUnix;

    @JsonProperty("appointment_id")
    private long appointmentId;
  }
}