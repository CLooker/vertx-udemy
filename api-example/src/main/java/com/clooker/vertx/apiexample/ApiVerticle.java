package com.clooker.vertx.apiexample;

import com.clooker.vertx.apiexample.entity.Product;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import java.util.ArrayList;
import java.util.List;

public class ApiVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiVerticle.class);

  public static void main(String[] args) {
    // can create config on the fly
    //    DeploymentOptions deploymentOptions = new DeploymentOptions();
    //    deploymentOptions.setConfig(
    //        new JsonObject().put("http.port", 3000)
    //    );

    Vertx vertx = Vertx.vertx();

    // or can set config in resources/conf/config.json
    ConfigRetriever configRetriever = ConfigRetriever.create(vertx);
    configRetriever.getConfig(config -> {
      if (config.succeeded()) {
        JsonObject configJson = config.result();
        System.out.println(configJson.encodePrettily());
        DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(configJson);
        vertx.deployVerticle(new ApiVerticle(), deploymentOptions);
      }
    });

  }

  private void getAllProducts(RoutingContext routingContext) {
    Product product1234 = new Product("item 1234", 1234);
    Product product2345 = new Product("item 2345", 2345);
    List<Product> products = new ArrayList<>();
    products.add(product1234);
    products.add(product2345);
    JsonObject response = new JsonObject().put("products", products);

    routingContext
        .response()
        .setStatusCode(200)
        .putHeader("content-type", "application/json")
        .end(Json.encodePrettily(response));

//    routingContext
//        .response()
//        .setStatusCode(400)
//        .putHeader("content-type", "application/json")
//        .end(Json.encodePrettily(new JsonObject().put("error", "Could not find products")));
  }

  @Override
  public void start() {
    LOGGER.info("apiVerticle started");

    Router router = Router.router(vertx);

    // API routes
    router.get("/api/v1/products").handler(this::getAllProducts);

    // default handler
    router.route().handler(StaticHandler.create().setCachingEnabled(false));

    // create server
    vertx
        .createHttpServer()
        .requestHandler(router::accept)
        .listen(
            config().getInteger("HTTP.PORT")
        );
  }

  @Override
  public void stop() {
    LOGGER.info("apiVerticle stopped");
  }
}
