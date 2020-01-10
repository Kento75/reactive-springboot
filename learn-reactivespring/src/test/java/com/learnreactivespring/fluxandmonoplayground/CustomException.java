package com.learnreactivespring.fluxandmonoplayground;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomException extends Throwable {

  private String message;

  public CustomException(Throwable e) {
    this.message = e.getMessage();
  }
}
