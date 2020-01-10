package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxAndMonoTest {

  @Test
  @DisplayName("簡単な Pub/Sub")
  public void FluxTest() {

    Flux<String> stringFlux =
        Flux.just("Spring", "Spring Boot", "Reactive Spring")
            //            .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
            .concatWith(Flux.just("After Error"))
            .log();

    stringFlux.subscribe(
        System.out::println,
        (e) -> System.err.println("Exception is " + e),
        () -> System.out.println("Completed"));
  }
}
