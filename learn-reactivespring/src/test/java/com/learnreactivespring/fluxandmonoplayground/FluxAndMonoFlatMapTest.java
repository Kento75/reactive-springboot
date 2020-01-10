package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoFlatMapTest {

  private List<String> convertToList(String s) {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return Arrays.asList(s, "newValue");
  }

  @Test
  public void transformUsingFlatMap() {

    Flux<String> stringFlux =
        Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) // A, B, C, D, E, F
            .flatMap(
                s -> {
                  return Flux.fromIterable(
                      convertToList(s)); // A -> List[A, newValue], B -> List[B, newValue] ...
                }); // db or external service call that returns a flux -> s -> Flux<String>

    StepVerifier.create(stringFlux)
        .expectNextCount(12) // A, B, C ... + newValue * 6
        .verifyComplete();
  }

  @Test
  public void transformUsingFlatMap_UsingParallel() {

    Flux<String> stringFlux =
        Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) // Flux<String>
            .window(2) // Flux<Flux<String>> -> (A, B), (C, D), (E, F)
            .flatMap(
                (s) -> s.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>>
            .flatMap(s -> Flux.fromIterable(s)) // Flux<String>
            .log(); // db or external service call that returns a flux -> s -> Flux<String>

    StepVerifier.create(stringFlux)
        .expectNextCount(12) // A, B, C ... + newValue * 6
        .verifyComplete();
  }

  @Test
  public void transformUsingFlatMap_UsingParallel_maintain_order() {

    Flux<String> stringFlux =
        Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) // Flux<String>
            .window(2) // Flux<Flux<String>> -> (A, B), (C, D), (E, F)
            .concatMap(
                (s) -> s.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>>
            .flatMap(s -> Flux.fromIterable(s)) // Flux<String>
            .log(); // db or external service call that returns a flux -> s -> Flux<String>

    StepVerifier.create(stringFlux)
        .expectNextCount(12) // A, B, C ... + newValue * 6
        .verifyComplete();
  }

  // concatMapに比べて早いというか、並行で実行できる
  @Test
  public void transformUsingFlatMap_UsingParallel_maintain_order_Sequential() {

    Flux<String> stringFlux =
        Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) // Flux<String>
            .window(2) // Flux<Flux<String>> -> (A, B), (C, D), (E, F)
            .flatMapSequential(
                (s) -> s.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>>
            .flatMap(s -> Flux.fromIterable(s)) // Flux<String>
            .log(); // db or external service call that returns a flux -> s -> Flux<String>

    StepVerifier.create(stringFlux)
        .expectNextCount(12) // A, B, C ... + newValue * 6
        .verifyComplete();
  }
}
