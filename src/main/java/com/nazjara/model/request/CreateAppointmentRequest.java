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
public class CreateAppointmentRequest {

  private List<Appointment> appointments;

  @JsonProperty("group_id")
  private String groupId;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Appointment {

    private long dateUnix;
    private long dateUtcUnix;

    @JsonProperty("location_id")
    private int locationId;

    @JsonProperty("service_id")
    private int serviceId;

    @JsonProperty("staff_id")
    private String staffId;

    private String startTime;
    private int originalSlot;
  }
}