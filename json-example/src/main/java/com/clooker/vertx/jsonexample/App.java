package com.clooker.vertx.jsonexample;

import io.vertx.core.json.JsonObject;

public class App {

  public static void main(String[] args) {
    Item item = new Item();
    item.setName("Chad");
    item.setDescription("programmer");

    JsonObject itemJson = JsonObject.mapFrom(item);
    System.out.println("itemJson: " + itemJson);

    Item item2 = itemJson.mapTo(Item.class);
    System.out.println("item2.name: " + item2.getName());
  }
}
