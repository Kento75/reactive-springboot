package com.learnreactivespring.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


// JUnit5
@ExtendWith(SpringExtension.class)
@WebFluxTest
class FluxAndMonoControllerTest {

  @Autowired
  WebTestClient webTestClient;

  @BeforeEach
  public void setUp() {}

  @Test
  @DisplayName("/flux - StepVerifier使うパターン")
  public void flux_approach1() {

    Flux<Integer> integerFlux = webTestClient.get().uri("/flux")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .returnResult(Integer.class)
        .getResponseBody();

    StepVerifier.create(integerFlux)
        .expectSubscription()
        .expectNext(1)
        .expectNext(2)
        .expectNext(3)
        .expectNext(4)
        .verifyComplete();
  }

  @Test
  @DisplayName("/flux - webTestClientでそのまま使うパターン")
  public void flux_approach2() {

    webTestClient.get().uri("/flux")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()  // status code is 200
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Integer.class) // [1,2,3,4] -> List<Integer>
        .hasSize(4); // [1,2,3,4] -> 4
  }

  @Test
  @DisplayName("/flux - JUnitのassertion使うパターン")
  public void flux_approach3() {

    List<Integer> expectedIntegerList = Arrays.asList(1,2,3,4);

    EntityExchangeResult<List<Integer>> entityExchangeResult =
        webTestClient.get().uri("/flux")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()  // status code is 200
            .expectBodyList(Integer.class)
            .returnResult();

    assertEquals(expectedIntegerList, entityExchangeResult.getResponseBody());
  }

  @Test
  @DisplayName("/flux - consureWith内で、JUnitのassertion使うパターン")
  public void flux_approach4() {

    List<Integer> expectedIntegerList = Arrays.asList(1,2,3,4);

        webTestClient.get().uri("/flux")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()  // status code is 200
            .expectBodyList(Integer.class)
            .consumeWith((response) -> {
              assertEquals(expectedIntegerList, response.getResponseBody());
            });
  }

  /* flux stream test cases */

  @Test
  @DisplayName("/fluxstream")
  public void fluxStream() {
    Flux<Long> longStreamFlux = webTestClient.get().uri("/fluxstream")
        .accept(MediaType.APPLICATION_STREAM_JSON)  // stream json
        .exchange()
        .expectStatus().isOk()
        .returnResult(Long.class)
        .getResponseBody();

    StepVerifier.create(longStreamFlux.log())
        .expectNext(0l)
        .expectNext(1l)
        .expectNext(2l)
        .thenCancel()
        .verify();
  }

}
