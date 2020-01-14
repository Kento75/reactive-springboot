package com.learnreactivespring.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// ↓ @Entityみたいな
@Document
@Data
@NoArgsConstructor
// @AllArgsConstructor ← なぜか動かない
public class Item {

  @Id
  private String id;
  private String description;
  private Double price;

  // AllArgusConstructorが動作しないので追記
  public Item(String id, String description, Double price) {
    this.id = id;
    this.description = description;
    this.price = price;
  }
}
