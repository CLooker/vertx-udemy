package com.clooker.vertx.mongoexample;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
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
    VertxOptions vertxOptions = new VertxOptions().setClustered(true);
    Vertx.clusteredVertx(vertxOptions, results -> {
      if (results.succeeded()) {
        Vertx vertx = results.result();
        vertx.deployVerticle(new MongoVerticle());
        mongoClient = MongoClient.createShared(
            vertx,
            new JsonObject()
                .put("connection_string", "mongodb://localhost:27017/testDB")
                .put("useObjectId", true)
        );
      }
    });
  }

  @Override
  public void start() {
    LOGGER.info("mongoVerticle started");
    Router router = Router.router(vertx);
    router.get("/products").handler(this::getProducts);
    vertx.createHttpServer().requestHandler(router::accept).listen(3000);
    vertx.eventBus().consumer("com.clooker.vertx.messaging", message -> {
      System.out.println("message when received: " + message.body());
      JsonObject replyMessage = new JsonObject()
          .put("responseCode", "OK")
          .put("message", "This is a reply to " + message.body());
      System.out.println("replyMessage when sent: " + replyMessage);
      message.reply(replyMessage);
    });

    vertx.setTimer(2000, handler -> {
      JsonObject obj = new JsonObject().put("info", "Hi");
      String message = obj.toString();
      System.out.println("message when sent: " + message);
      vertx.eventBus().send("com.clooker.vertx.messaging", message, reply -> {
        if (reply.succeeded()) {
          String replyMessage = reply.result().body().toString();
          System.out.println("replyMessage when received: " + replyMessage);
        }
      });
    });
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
