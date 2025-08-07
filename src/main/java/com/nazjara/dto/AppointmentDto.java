package com.nazjara.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {

  private long id;
  private long dateUnix;
  private long dateUtcUnix;
  private int locationId;
  private int serviceId;
  private String staffId;
  private String startTime;
}