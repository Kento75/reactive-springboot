package con.learnreactivespring.controller;

import con.learnreactivespring.domain.Item;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@RestController
public class ItemClientController {

  WebClient webClient = WebClient.create("http://localhost:8080");

  @GetMapping("/client/retrieve")
  public Flux<Item> getAllItemsUsingRetrieve() {

    return webClient
        .get()
        .uri("/v1/items")
        .retrieve() // body情報のみ
        .bodyToFlux(Item.class)
        .log("Items in Client Project : ");
  }

  @GetMapping("/client/exchange")
  public Flux<Item> getAllItemsUsingExchange() {

    return webClient
        .get()
        .uri("/v1/items")
        .exchange() // ステータスコード、body情報
        .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Item.class))
        .log("Items in Client Project exchange : ");
  }
}
