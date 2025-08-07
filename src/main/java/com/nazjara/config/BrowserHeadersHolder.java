package com.nazjara.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class BrowserHeadersHolder {

  private final Map<String, String> headers = new HashMap<>();

  {
    // Essential headers
    headers.put("User-Agent",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36");
    headers.put("Accept", "*/*");
    headers.put("Accept-Language", "en-US,en;q=0.9,uk;q=0.8,ro;q=0.7");
    headers.put("Accept-Encoding", "gzip, deflate, br, zstd");
    headers.put("Connection", "keep-alive");

    // CORS and origin headers
    headers.put("Referer", "https://www.calendis.ro");
    headers.put("Origin", "https://www.calendis.ro");

    // Browser fingerprinting
    headers.put("Sec-CH-UA",
        "\"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"138\", \"Google Chrome\";v=\"138\"");
    headers.put("Sec-CH-UA-Mobile", "?0");
    headers.put("Sec-CH-UA-Platform", "\"Linux\"");
    headers.put("Sec-Fetch-Dest", "empty");
    headers.put("Sec-Fetch-Mode", "cors");
    headers.put("Sec-Fetch-Site", "same-origin");
    headers.put("DNT", "1");
  }

  public Map<String, String> getHeaders() {
    return new HashMap<>(headers);
  }

  public Map<String, String> withCookie(String cookie) {
    var headersWithCookie = new HashMap<>(headers);
    headersWithCookie.put(HttpHeaders.COOKIE, cookie);
    return headersWithCookie;
  }
}
