package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class VirtualTimeTest {

  @Test
  void testingWithoutVirtualTime() {

    Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1)).take(3); // 0L, 1L, 2L

    StepVerifier.create(longFlux.log())
        .expectSubscription()
        .expectNext(0L, 1L, 2L)
        .verifyComplete();
  }

  @Test
  @DisplayName("時間を自分で操作してテスト")
  void testingWithVirtualTime() {

    VirtualTimeScheduler.getOrSet();

    Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1)).take(3); // 0L, 1L, 2L

    StepVerifier.withVirtualTime(() -> longFlux.log())
        .expectSubscription()
        .thenAwait(Duration.ofSeconds(3))
        .expectNext(0L, 1L, 2L)
        .verifyComplete();
  }
}
