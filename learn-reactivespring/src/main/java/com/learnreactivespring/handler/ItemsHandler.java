package com.learnreactivespring.handler;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ItemsHandler {

  @Autowired ItemReactiveRepository itemReactiveRepository;

  static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

  public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {

    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(itemReactiveRepository.findAll(), Item.class);
  }

  public Mono<ServerResponse> getOneItem(ServerRequest serverRequest) {

    String itemId = serverRequest.pathVariable("id");
    Mono<Item> itemMono = itemReactiveRepository.findById(itemId);

    return itemMono
        .flatMap(
            item ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(item)))
        .switchIfEmpty(notFound);
  }

  public Mono<ServerResponse> createItem(ServerRequest serverRequest) {

    Mono<Item> itemTobeInserted = serverRequest.bodyToMono(Item.class);

    return itemTobeInserted.flatMap(
        item ->
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.save(item), Item.class));
  }

  public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {

    String itemId = serverRequest.pathVariable("id");
    Mono<Void> deleteItem = itemReactiveRepository.deleteById(itemId);

    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(deleteItem, Void.class);
  }

  public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {

    String itemId = serverRequest.pathVariable("id");

    Mono<Item> updatedItem =
        serverRequest
            .bodyToMono(Item.class)
            .flatMap(
                item -> {
                  Mono<Item> itemMono =
                      itemReactiveRepository
                          .findById(itemId)
                          .flatMap(
                              currentItem -> {
                                currentItem.setDescription(item.getDescription());
                                currentItem.setPrice(item.getPrice());

                                return itemReactiveRepository.save(currentItem);
                              });
                  return itemMono;
                });

    return updatedItem
        .flatMap(
            item ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(item)))
        .switchIfEmpty(notFound);
  }
}
