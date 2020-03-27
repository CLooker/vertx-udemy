package com.clooker.vertx.finalapi.database;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

public class MongoManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoManager.class);
  private MongoClient mongoClient = null;

  public MongoManager(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }

  public void registerConsumer(Vertx vertx) {
    vertx.eventBus().consumer("com.clooker.vertx.database.messages", message -> {
      JsonObject body = (JsonObject) message.body();
      switch (body.getString("cmd")) {
        case ("getProducts"): getProducts(message);
        case ("getProductById"): getProductById(message, body.getString("id"));
        default: break;
      }
    });
  }

  private void getProductById(Message<Object> message, String id) {
    mongoClient.find(
        "products",
        new JsonObject().put("id", id),
        asyncResult -> message.reply(new JsonObject().put("products", asyncResult.result()))
    );
  }

  private void getProducts(Message<Object> message) {
    mongoClient.find(
        "products",
        new JsonObject(),
        asyncResult -> message.reply(new JsonObject().put("products", asyncResult.result()))
    );
  }
}
