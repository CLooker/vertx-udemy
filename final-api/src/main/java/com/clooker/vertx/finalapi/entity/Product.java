package com.clooker.vertx.finalapi.entity;

public class Product {
  private String description;
  private String id;
  private int number;

  public Product(String description, String id, int number) {
    this.description = description;
    this.id = id;
    this.number = number;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }
}