package com.clooker.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class BasicWebVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(BasicWebVerticle.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new BasicWebVerticle());
  }

  @Override
  public void start() {
    LOGGER.info("basicWebVerticle verticle started");

//    vertx
//        .createHttpServer()
//        .requestHandler(routingContext -> {
//          routingContext
//              .response()
//              .end("<h1>Hello World!</h1>");
//        })
//        .listen(3000);

    Router router = Router.router(vertx);

    router.get("/api/v1/products").handler(this::getAllProducts);

    router.get("/yo.html").handler(routingContext -> {
      ClassLoader classLoader = getClass().getClassLoader();
      File file = new File(classLoader.getResource("webroot/yo.html").getFile());

      String mappedHTML = "";
      try {
        StringBuilder result = new StringBuilder("");
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
          String line = scanner.nextLine();
          result.append(line).append("\n");
        }
        scanner.close();
        mappedHTML = result.toString().replaceAll("\\{name}", "Chad");
      } catch (IOException e) {
        e.printStackTrace();
      }

      routingContext.response().putHeader("content-type", "text/html").end(mappedHTML);
    });

    // default request handler
    router.route().handler(StaticHandler.create().setCachingEnabled(false));
    vertx.createHttpServer().requestHandler(router::accept).listen(3000);
  }

  @Override
  public void stop() {
    LOGGER.info("basicWebVerticle verticle stopped");
  }

  private void getAllProducts(RoutingContext routingContext) {
    JsonObject response = new JsonObject();
    response
        .put("itemNumber", "123")
        .put("itemDescription", "my item 123");
    routingContext
        .response()
        .setStatusCode(200)
        .putHeader("content-type", "application/json")
        .end(Json.encodePrettily(response));
  }
}
