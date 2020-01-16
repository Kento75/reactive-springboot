package com.learnreactivespring.handler;

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

import java.util.Arrays;
import java.util.List;

import static com.learnreactivespring.constants.ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ItemsHandlerTest {

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

    // テストデータ作成
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
  @DisplayName("全件取得 - func route")
  void getAllItems() {

    webTestClient
        .get()
        .uri(ITEM_FUNCTIONAL_END_POINT_V1)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Item.class)
        .hasSize(4);
  }

  @Test
  @DisplayName("検索 - func route - ID")
  public void getOneItem() {

    webTestClient
        .get()
        .uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABC")
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
  @DisplayName("検索 - func route - ID Not Found")
  public void getOneItem_notFound() {

    webTestClient
        .get()
        .uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "XDXD")
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  @DisplayName("作成 - func route")
  public void createItem() {

    Item item = new Item(null, "Test create item", 222.22);

    webTestClient
        .post()
        .uri(ITEM_FUNCTIONAL_END_POINT_V1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(item), Item.class)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isNotEmpty()
        .jsonPath("$.description")
        .isEqualTo("Test create item")
        .jsonPath("$.price")
        .isEqualTo(222.22);
  }

  @Test
  @DisplayName("削除 - func route")
  public void deleteItem() {

    webTestClient
        .delete()
        .uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABC")
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("更新 - func route")
  public void updateItem() {

    // given
    Item item = new Item(null, "TESTESTETS", 123.456);

    webTestClient
        .put()
        .uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABC")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(item), Item.class)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.price")
        .isEqualTo(123.456)
        .jsonPath("$.description")
        .isEqualTo("TESTESTETS")
        .jsonPath("$.id")
        .isEqualTo("ABC");
  }

  @Test
  @DisplayName("更新 - func route - Not Found")
  public void updateItem_notFound() {

    // given
    Item item = new Item(null, "TESTESTETS", 123.456);

    webTestClient
        .put()
        .uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "AWSWSWSWS")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(item), Item.class)
        .exchange()
        .expectStatus()
        .isNotFound();
  }
}
