package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class ItemReactiveRepositoryTest {

  @Autowired
  ItemReactiveRepository itemReactiveRepository;

  // テストデータ
  List<Item> itemList = Arrays.asList(new Item(null, "Samsung TV", 400.0),
      new Item(null, "LG TV", 420.0),
      new Item(null, "Apple Watch", 299.99),
      new Item(null, "Beats Headphones", 149.99),
      new Item("ABC", "Bose Headphones", 149.99));

  @BeforeEach
  public void setUp() {

    itemReactiveRepository.deleteAll()
        .thenMany(Flux.fromIterable(itemList))
        .flatMap(itemReactiveRepository::save)  // テストデータを登録
        .doOnNext((item -> {
          System.out.println("Inserted Item is : " + item);
        }))
        .blockLast();
  }

  @Test
  @DisplayName("全件取得")
  public void getAllItems() {

    StepVerifier.create(itemReactiveRepository.findAll())  // 5 items
      .expectSubscription()
      .expectNextCount(5)
      .verifyComplete();
  }

  @Test
  @DisplayName("1検索 - itemId")
  public void getItemById() {

    StepVerifier.create(itemReactiveRepository.findById("ABC").log("getItemById : "))
        .expectSubscription()
        .expectNextMatches((item -> item.getDescription().equals("Bose Headphones")))
        .verifyComplete();
  }

  @Test
  @DisplayName("詳細で検索")
  public void findItemByDescription() {

    StepVerifier.create(itemReactiveRepository.findByDescription("Bose Headphones").log("findItemByDescription : "))
        .expectSubscription()
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  @DisplayName("アイテム作成")
  public void saveItem() {

    // given
    Item item = new Item("DEF", "Google Home Mini", 30.00);

    Mono<Item> saveItem = itemReactiveRepository.save(item);

    StepVerifier.create(saveItem.log("saveItem : "))
        .expectSubscription()
        .expectNextMatches(item1 -> (item.getId() != null && item1.getDescription().equals("Google Home Mini")))
        .verifyComplete();

  }

  @Test
  @DisplayName("アイテム更新")
  public void updateItem() {

    // given
    double newPrice = 555.5;

    Flux<Item> updatedItem = itemReactiveRepository.findByDescription("LG TV").map(item -> {
      item.setPrice(newPrice);  // 値差し替え
      return item;
    }).flatMap(item -> {
      return itemReactiveRepository.save(item); // 更新
    });

    StepVerifier.create(updatedItem.log("updateItem : "))
        .expectSubscription()
        .expectNextMatches(item -> item.getPrice() == 555.5)
        .verifyComplete();
  }
}
