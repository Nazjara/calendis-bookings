package com.nazjara.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateTimeUtils {

  public long getFutureTimestamp(String zoneId, int daysAhead, String time) {
    return LocalDate.now()
        .plusDays(daysAhead)
        .atStartOfDay(ZoneId.of(zoneId))
        .plusSeconds(time != null ?
            LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")).toSecondOfDay() : 0)
        .toInstant()
        .getEpochSecond();
  }

  public long convertTimestamp(long timestampSeconds, String fromZoneId, String toZoneId) {
    var sourceDateTime = Instant.ofEpochSecond(timestampSeconds)
        .atZone(ZoneId.of(fromZoneId));
    var targetDateTime = sourceDateTime.withZoneSameInstant(ZoneId.of(toZoneId));
    return targetDateTime.toInstant().getEpochSecond();
  }
}
