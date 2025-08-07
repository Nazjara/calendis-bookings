package com.nazjara.config;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

  @Value("${calendis.api.base-url}")
  private String baseUrl;

  @Bean
  public RestClient restClient() {
    var httpClient = HttpClientBuilder.create()
        .build();

    return RestClient.builder()
        .baseUrl(baseUrl)
        .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
        .build();
  }
}