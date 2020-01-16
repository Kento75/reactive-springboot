package con.learnreactivespring.controller;

import con.learnreactivespring.domain.Item;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
        .exchange() // ステータスコード、ヘッダー、body情報
        .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Item.class))
        .log("Items in Client Project exchange : ");
  }

  @GetMapping("/client/retrieve/singleItem/{id}")
  public Mono<Item> getOneItemsUsingRetrieve(@PathVariable("id") String itemId) {

    return webClient
        .get()
        .uri("/v1/items/{id}", itemId)
        .retrieve() // body情報のみ
        .bodyToMono(Item.class)
        .log("Items in Client Project retrieve single item : ");
  }

  @GetMapping("/client/exchange/singleItem/{id}")
  public Mono<Item> getOneItemsUsingExchange(@PathVariable("id") String itemId) {

    return webClient
        .get()
        .uri("/v1/items/{id}", itemId)
        .exchange() // body情報 + Statusコード + ヘッダー
        .flatMap(clientResponse -> clientResponse.bodyToMono(Item.class))
        .log("Items in Client Project exchange single item : ");
  }

  @PostMapping("/client/createItem")
  public Mono<Item> createItem(@RequestBody Item item) {

    return webClient
        .post()
        .uri("/v1/items")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(item), Item.class)
        .retrieve()
        .bodyToMono(Item.class)
        .log("Create item is : ");
  }

  @PutMapping("/client/updateItem/{id}")
  public Mono<Item> updateItem(@PathVariable("id") String itemId, @RequestBody Item item) {

    return webClient
        .put()
        .uri("/v1/items/{id}", itemId)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(item), Item.class)
        .retrieve()
        .bodyToMono(Item.class)
        .log("Update Item is : ");
  }
}
