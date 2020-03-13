package com.clooker.vertx.formalapiexample;

import com.clooker.vertx.formalapiexample.resources.ProductResources;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.StaticHandler;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

public class ApiVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiVerticle.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    ConfigRetriever configRetriever = ConfigRetriever.create(vertx);
    configRetriever.getConfig(config -> {
      if (config.succeeded()) {
        JsonObject configJson = config.result();
        DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(configJson);
        vertx.deployVerticle(new ApiVerticle(), deploymentOptions);
      }
    });
  }

  @Override
  public void start() {
    LOGGER.info("apiVerticle started");
    Router router = Router
        .router(vertx)
        .mountSubRouter("/api/", new ProductResources().getRouter(vertx));

    router.route().handler(CookieHandler.create());

    router.get("/yo.html").handler(routingContext -> {
      Optional<String> nameOpt = Optional.ofNullable(routingContext.getCookie("name").getValue());

      ClassLoader classLoader = getClass().getClassLoader();
      File file = new File(classLoader.getResource("webroot/yo.html").getFile());

      // basic templating
      String mappedHTML = "";
      try {
        StringBuilder result = new StringBuilder();
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
          String line = scanner.nextLine();
          result.append(line).append("\n");
        }
        scanner.close();
        mappedHTML = result
            .toString()
            .replaceAll(
                "\\{name}",
                nameOpt.orElse("unknown")
            );
      } catch (IOException e) {
        e.printStackTrace();
      }

      routingContext.response().putHeader("content-type", "text/html").end(mappedHTML);
    });

    // default handler
    router.route().handler(StaticHandler.create().setCachingEnabled(false));

    router.errorHandler(500, rc -> {
      LOGGER.error("Handling failure");
      Throwable failure = rc.failure();
      if (failure != null) {
        failure.printStackTrace();
      }
    });

    // create server
    vertx
        .createHttpServer()
        .requestHandler(router::accept)
        .listen(
            config().getInteger("HTTP.PORT"),
            httpServerAsyncResult -> {
              if (httpServerAsyncResult.succeeded()) {
                LOGGER.info("apiVerticle running on port " + config().getInteger("HTTP.PORT"));
              } else {
                LOGGER.error("apiVerticle failed to start: " + httpServerAsyncResult.cause());
              }
            }
        );
  }

  @Override
  public void stop() {
    LOGGER.info("apiVerticle stopped");
  }
}