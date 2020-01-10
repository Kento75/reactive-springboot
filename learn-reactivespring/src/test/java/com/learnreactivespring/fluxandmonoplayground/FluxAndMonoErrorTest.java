package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoErrorTest {

  @Test
  public void FluxErrorHandling() {

    Flux<String> stringFlux =
        Flux.just("A", "B", "C")
            .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
            .concatWith(Flux.just("D")) // ここ実行されない
            .onErrorResume(
                (e) -> { // this block gets executed
                  System.out.println("Exception is: " + e);
                  return Flux.just("default", "default1");
                });

    StepVerifier.create(stringFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C")
        .expectNext("default", "default1")
        .verifyComplete();
  }

  @Test
  public void FluxErrorHandling_OnErrorReturn() {

    Flux<String> stringFlux =
        Flux.just("A", "B", "C")
            .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
            .concatWith(Flux.just("D")) // ここ実行されない
            .onErrorReturn("default");

    StepVerifier.create(stringFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C")
        .expectNext("default")
        .verifyComplete();
  }

  @Test
  public void FluxErrorHandling_OnErrorMap() {

    Flux<String> stringFlux =
        Flux.just("A", "B", "C")
            .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
            .concatWith(Flux.just("D")) // ここ実行されない
            .onErrorMap((e) -> new CustomException(e));

    StepVerifier.create(stringFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C")
        .expectError(CustomException.class)
        .verify();
  }

  @Test
  public void FluxErrorHandling_OnErrorMap_withRetry() {

    Flux<String> stringFlux =
        Flux.just("A", "B", "C")
            .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
            .concatWith(Flux.just("D")) // ここ実行されない
            .onErrorMap((e) -> new CustomException(e)) // エラーは最後にレスポンス
            .retry(2); // justを2回、再実行

    StepVerifier.create(stringFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C")
        .expectNext("A", "B", "C")
        .expectNext("A", "B", "C")
        .expectError(CustomException.class)
        .verify();
  }

  @Test
  public void FluxErrorHandling_OnErrorMap_withRetryBackoff() {

    Flux<String> stringFlux =
        Flux.just("A", "B", "C")
            .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
            .concatWith(Flux.just("D")) // ここ実行されない
            .onErrorMap((e) -> new CustomException(e)) // エラーは最後にレスポンス
            .retryBackoff(2, Duration.ofSeconds(5)); // justを2回、再実行 5秒のインターバル

    StepVerifier.create(stringFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C")
        .expectNext("A", "B", "C")
        .expectNext("A", "B", "C")
        .expectError(IllegalStateException.class)
        .verify();
  }
}
