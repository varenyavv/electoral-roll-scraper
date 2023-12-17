package com.raw.scraper.service;

import com.raw.scraper.constant.NepalState;
import com.raw.scraper.model.GetRollRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
public class NepalVoterListClient {

  public static final String INDEX_PROCESS_1_PHP = "/index_process_1.php";
  public static final String VIEW_WARD_1_PHP = "/view_ward_1.php";
  private final WebClient webClient;

  NepalVoterListClient() {
    this.webClient = createWebClient();
  }

  private WebClient createWebClient() {
    final int size = 16 * 1024 * 1024;
    final ExchangeStrategies strategies =
        ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
            .build();
    return WebClient.builder()
        .baseUrl("https://voterlist.election.gov.np/bbvrs1")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .exchangeStrategies(strategies)
        .build();
  }

  public String getDistrictsByState(NepalState nepalState) {
    return webClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(INDEX_PROCESS_1_PHP).build())
        .accept(MediaType.APPLICATION_JSON)
        .body(
            BodyInserters.fromFormData("state", String.valueOf(nepalState.getKey()))
                .with("list_type", "district"))
        .retrieve()
        .bodyToMono(String.class)
        .block(Duration.of(10, ChronoUnit.SECONDS));
  }

  public String getVdcListByDistrict(int district) {
    return webClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(INDEX_PROCESS_1_PHP).build())
        .accept(MediaType.APPLICATION_JSON)
        .body(
            BodyInserters.fromFormData("district", String.valueOf(district))
                .with("list_type", "vdc"))
        .retrieve()
        .bodyToMono(String.class)
        .block(Duration.of(10, ChronoUnit.SECONDS));
  }

  public String getWardsByVdc(int vdc) {
    return webClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(INDEX_PROCESS_1_PHP).build())
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromFormData("vdc", String.valueOf(vdc)).with("list_type", "ward"))
        .retrieve()
        .bodyToMono(String.class)
        .block(Duration.of(10, ChronoUnit.SECONDS));
  }

  public String getRegistrationCentersByWardAndVdc(int vdc, int ward) {
    return webClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(INDEX_PROCESS_1_PHP).build())
        .accept(MediaType.APPLICATION_JSON)
        .body(
            BodyInserters.fromFormData("vdc", String.valueOf(vdc))
                .with("ward", String.valueOf(ward))
                .with("list_type", "reg_centre"))
        .retrieve()
        .bodyToMono(String.class)
        .block(Duration.of(10, ChronoUnit.SECONDS));
  }

  public String getVoterRoll(GetRollRequest getRollRequest) {
    return webClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(VIEW_WARD_1_PHP).build())
        .accept(MediaType.TEXT_HTML)
        .body(
            BodyInserters.fromFormData("state", String.valueOf(getRollRequest.getState().getKey()))
                .with("district", String.valueOf(getRollRequest.getDistrict().getKey()))
                .with("vdc_mun", String.valueOf(getRollRequest.getVdc().getKey()))
                .with("ward", String.valueOf(getRollRequest.getWard().getKey()))
                .with("reg_centre", String.valueOf(getRollRequest.getRegCenter().getKey())))
        .retrieve()
        .bodyToMono(String.class)
        .block(Duration.of(30, ChronoUnit.SECONDS));
  }
}
