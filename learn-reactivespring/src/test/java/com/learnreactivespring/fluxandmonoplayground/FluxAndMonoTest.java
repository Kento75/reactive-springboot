package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

  /* Flux テストケース */
  @Test
  @DisplayName("簡単な Pub/Sub")
  public void fluxTest() {

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

  @Test
  @DisplayName("簡単な Pub/Sub - テストケース")
  public void fluxTestElements() {
    Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring").log();

    // onNextとonCompleteの順番が等しい場合GREEN
    StepVerifier.create(stringFlux)
        .expectNext("Spring")
        .expectNext("Spring Boot")
        .expectNext("Reactive Spring")
        .verifyComplete();
  }

  @Test
  @DisplayName("簡単な Pub/Sub - テストケース 短縮形")
  public void fluxTestElements_case2() {
    Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring").log();

    // onNextとonCompleteの順番が等しい場合GREEN
    StepVerifier.create(stringFlux)
        .expectNext("Spring", "Spring Boot", "Reactive Spring")
        .verifyComplete();
  }

  @Test
  @DisplayName("簡単な Pub/Sub - テストケース (エラー)")
  public void fluxTestElements_WithoutError() {
    Flux<String> stringFlux =
        Flux.just("Spring", "Spring Boot", "Reactive Spring")
            .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
            .log();

    StepVerifier.create(stringFlux)
        .expectNext("Spring")
        .expectNext("Spring Boot")
        .expectNext("Reactive Spring")
        .expectErrorMessage("Exception Occurred")
        .verify();
  }

  @Test
  @DisplayName("簡単な Pub/Sub - テストケース (カウント and エラー)")
  public void fluxTestElementsCount_WithoutError() {
    Flux<String> stringFlux =
        Flux.just("Spring", "Spring Boot", "Reactive Spring")
            .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
            .log();

    StepVerifier.create(stringFlux)
        .expectNextCount(3)
        .expectErrorMessage("Exception Occurred")
        .verify();
  }

  /* Mono テストケース */

  @Test
  @DisplayName("シンプルなMono テスト")
  public void monoTest() {
    Mono<String> stringMono = Mono.just("Spring");

    StepVerifier.create(stringMono.log()).expectNext("Spring").verifyComplete();
  }

  @Test
  @DisplayName("シンプルなMono テスト - エラー")
  public void monoTest_Error() {

    StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")).log())
        .expectError(RuntimeException.class)
        .verify();
  }
}
