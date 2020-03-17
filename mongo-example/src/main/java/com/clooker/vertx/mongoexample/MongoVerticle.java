package com.clooker.vertx.mongoexample;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.util.List;

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
    vertx.createHttpServer().requestHandler(router::accept).listen(3000);
  }

  private void getProducts(RoutingContext routingContext) {
    LOGGER.info("getProducts called");
    mongoClient.find(
        "products",
        new JsonObject(),
        asyncResult -> routingContext
            .response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(Json.encodePrettily(new JsonObject().put("products", asyncResult.result())))
    );
  }

  @Override
  public void stop() {
    LOGGER.info("mongoVerticle stopped");
  }
}
