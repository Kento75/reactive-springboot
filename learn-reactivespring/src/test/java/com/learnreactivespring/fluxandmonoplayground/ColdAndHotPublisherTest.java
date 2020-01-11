package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;

public class ColdAndHotPublisherTest {

  @Test
  public void coldPublisherTest() throws InterruptedException {

    Flux<String> stringFlux =
        Flux.just("A", "B", "C", "D", "E", "F").delayElements(Duration.ofSeconds(1));

    stringFlux.subscribe(
        s ->
            System.out.println(
                "Subscriber 1 : "
                    + s
                    + " "
                    + LocalDateTime.now())); // emits the value from beginning

    Thread.sleep(2000);

    stringFlux.subscribe(
        s ->
            System.out.println(
                "Subscriber 2 : "
                    + s
                    + " "
                    + LocalDateTime.now())); // emits the value from beginning

    Thread.sleep(4000);
  }

  @Test
  public void hotPublisherTest() throws InterruptedException {

    Flux<String> stringFlux =
        Flux.just("A", "B", "C", "D", "E", "F").delayElements(Duration.ofSeconds(1));

    // hot publisher
    ConnectableFlux<String> connectableFlux = stringFlux.publish();
    connectableFlux.connect();

    connectableFlux.subscribe(
        (s) -> System.out.println("Subscriber 1 : " + s + " " + LocalDateTime.now()));

    Thread.sleep(3000);

    connectableFlux.subscribe(
        (s) ->
            System.out.println(
                "Subscriber 2 : "
                    + s
                    + " "
                    + LocalDateTime.now())); // does not emit the values from beginning

    Thread.sleep(4000);
  }
}
