package com.clooker.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class BasicWebVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(BasicWebVerticle.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new BasicWebVerticle());
  }

  @Override
  public void start() {
    LOGGER.info("basicWebVerticle verticle started");

    vertx
        .createHttpServer()
        .requestHandler(routingContext -> {
          routingContext
              .response()
              .end("<h1>Hello World!</h1>");
        })
        .listen(3000);
  }

  @Override
  public void stop() {
    LOGGER.info("basicWebVerticle verticle stopped");
  }
}
