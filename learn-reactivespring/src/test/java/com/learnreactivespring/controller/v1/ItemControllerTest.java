package com.learnreactivespring.controller.v1;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static com.learnreactivespring.constants.ItemConstants.ITEM_END_POINT_V1;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ItemControllerTest {

  @Autowired WebTestClient webTestClient;

  @Autowired ItemReactiveRepository itemReactiveRepository;

  public List<Item> data() {
    return Arrays.asList(
        new Item(null, "Samsung TV", 399.99),
        new Item(null, "LG TV", 329.99),
        new Item(null, "AppliWatch", 349.99),
        new Item("ABC", "Beats HeadPhones", 19.99));
  }

  @BeforeEach
  public void setUp() {
    // 初期データ登録
    itemReactiveRepository
        .deleteAll()
        .thenMany(Flux.fromIterable(data()))
        .flatMap(itemReactiveRepository::save)
        .doOnNext(
            (item -> {
              System.out.println("Inserted item is : " + item);
            }))
        .blockLast();
  }

  @Test
  @DisplayName("全件取得")
  public void getAllItems_approach1() {
    webTestClient
        .get()
        .uri(ITEM_END_POINT_V1)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Item.class)
        .hasSize(4);
  }

  @Test
  @DisplayName("全件取得 その２")
  public void getAllItems_approach2() {
    Flux<Item> itemFlux =
        webTestClient
            .get()
            .uri(ITEM_END_POINT_V1)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .returnResult(Item.class)
            .getResponseBody();

    StepVerifier.create(itemFlux.log("value from network : ")).expectNextCount(4).verifyComplete();
  }

  @Test
  @DisplayName("全件取得 - 取得したデータのIDがnullではない")
  public void getAllItems_id() {
    webTestClient
        .get()
        .uri(ITEM_END_POINT_V1)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Item.class)
        .hasSize(4)
        .consumeWith(
            (response) -> {
              List<Item> items = response.getResponseBody();
              items.forEach(
                  item -> {
                    assertTrue(item.getId() != null);
                  });
            });
  }

  @Test
  @DisplayName("検索 - ID")
  public void getOneItem() {

    webTestClient
        .get()
        .uri(ITEM_END_POINT_V1.concat("/{id}"), "ABC")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo("ABC")
        .jsonPath("$.price")
        .isEqualTo(19.99)
        .jsonPath("$.description")
        .isEqualTo("Beats HeadPhones");
  }

  @Test
  @DisplayName("検索 - ID Not Found")
  public void getOneItem_notFound() {

    webTestClient
        .get()
        .uri(ITEM_END_POINT_V1.concat("/{id}"), "XDXD")
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  @DisplayName("登録")
  public void createItem() {

    // given
    Item item = new Item(null, "Iphone Xs", 999.99);

    webTestClient
        .post()
        .uri(ITEM_END_POINT_V1)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(item), Item.class)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody()
        .jsonPath("$.id")
        .isNotEmpty()
        .jsonPath("$.description")
        .isEqualTo("Iphone Xs")
        .jsonPath("$.price")
        .isEqualTo(999.99);
  }

  @Test
  @DisplayName("削除")
  public void deleteItem() {

    webTestClient
        .delete()
        .uri(ITEM_END_POINT_V1.concat("/{id}"), "ABC")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Void.class);
  }
}
