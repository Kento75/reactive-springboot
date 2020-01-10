package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxANdMonoFilterTest {

  List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

  @Test
  public void filterTest() {

    Flux<String> namesFlux =
        Flux.fromIterable(names).filter(s -> s.startsWith("a")).log(); // adam, anna

    StepVerifier.create(namesFlux).expectNext("adam", "anna").verifyComplete();
  }

  @Test
  public void filterTest_Length() {

    Flux<String> namesFlux = Flux.fromIterable(names).filter(s -> s.length() > 4).log(); // jenny

    StepVerifier.create(namesFlux).expectNext("jenny").verifyComplete();
  }
}
