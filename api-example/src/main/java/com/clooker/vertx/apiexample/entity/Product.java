package com.clooker.vertx.apiexample.entity;

public class Product {
  private String description;
  private int number;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public Product(String description, int number) {
    this.description = description;
    this.number = number;
  }
}
