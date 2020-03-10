package com.clooker.vertx.webexample;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class WebVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebVerticle.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new WebVerticle());
  }

  @Override
  public void start() {
    LOGGER.info("webVerticle started");

    // Hello World
//    vertx.createHttpServer()
//        .requestHandler(routingContext -> {
//          routingContext.response().end("<h1>Hello World!</h1>");
//        })
//        .listen(3000);

    Router router = Router.router(vertx);

    // route handler
    router.get("/yo.html").handler(routingContext -> {
      // basic example where handler can read from fs

      ClassLoader classLoader = getClass().getClassLoader();
      File file = new File(classLoader.getResource("webroot/yo.html").getFile());

      // basic templating
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

    // default route handler
    // this serves from resources/webroot/$path
    // will serve index.html if no path
    router.route().handler(StaticHandler.create().setCachingEnabled(false));

    // create server
    vertx.createHttpServer().requestHandler(router::accept).listen(3000);
  }

  @Override
  public void stop() {
    LOGGER.info("webVerticle stopped");
  }
}
