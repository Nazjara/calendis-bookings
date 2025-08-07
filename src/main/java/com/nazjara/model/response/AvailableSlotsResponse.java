package com.nazjara.model.response;

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
public class AvailableSlotsResponse {

  private String message;
  private int success;

  @JsonProperty("available_slots")
  private List<Slot> availableSlots;

  @JsonProperty("service_details")
  private Object serviceDetails;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Slot {

    @JsonProperty("is_available")
    private int isAvailable;

    @JsonProperty("staff_id")
    private String staffId;

    private long time;

    @JsonProperty("group_id")
    private String groupId;
  }
}