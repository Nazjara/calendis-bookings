package com.nazjara.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "calendis.scheduler")
public class SchedulerConfig {

  private EmailConfig email = new EmailConfig();
  private PasswordConfig password = new PasswordConfig();
  private LocationsConfig laTerenuri = new LocationsConfig();
  private LocationsConfig gheorgheni = new LocationsConfig();
  private boolean remember = true;
  private int dayOnly = 1;

  @Data
  public static class EmailConfig {

    private String primary;
    private String secondary;
  }

  @Data
  public static class PasswordConfig {

    private String primary;
    private String secondary;
  }

  @Data
  public static class LocationsConfig {

    private int locationId;
    private SportServicesConfig tenis = new SportServicesConfig();
    private SportServicesConfig squash = new SportServicesConfig();
    private SportServicesConfig tenisTable = new SportServicesConfig();
  }

  @Data
  public static class SportServicesConfig {

    private int serviceId;
    private int stuffId;
    private ScheduleConfig primary = new ScheduleConfig();
    private ScheduleConfig secondary = new ScheduleConfig();
  }

  @Data
  public static class ScheduleConfig {

    private String cronExpression;
    private boolean enabled = false;
    private String appointmentTime;
  }
}