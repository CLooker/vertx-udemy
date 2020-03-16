package com.clooker.vertx.mongoexample;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MongoVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoVerticle.class);
  private static MongoClient mongoClient = null;

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    mongoClient = MongoClient.createShared(
        vertx,
        new JsonObject()
            .put("connection_string", "mongodb://localhost:27017/testDB")
            .put("useObjectId", true)
    );
    vertx.deployVerticle(new MongoVerticle());
  }

  @Override
  public void start() {
    LOGGER.info("mongoVerticle started");
    Router router = Router.router(vertx);
    router.get("/products").handler(this::getProducts);
  }

  private void getProducts(RoutingContext routingContext) {
    mongoClient.find(
        "products",
        new JsonObject(),
        result -> {
          LOGGER.info("getProducts result: " + result);
        }
    );
  }

  @Override
  public void stop() {
    LOGGER.info("mongoVerticle stopped");
  }
}
