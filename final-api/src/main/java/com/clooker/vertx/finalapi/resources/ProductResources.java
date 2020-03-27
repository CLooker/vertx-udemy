package com.clooker.vertx.finalapi.resources;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class ProductResources {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProductResources.class);
  private static final String mongoAddress = "com.clooker.vertx.database.messages";
  private Vertx vertx = null;

  public Router getRouter(Vertx vertx) {
    this.vertx = vertx;

    Router router = Router.router(vertx);
    router.route("/*").handler(this::middlewareHandler);

    // BodyHandler converts http req body into JSON
    router.route("/v1/products*").handler(BodyHandler.create());

    router.get("/v1/products").handler(this::getProducts);
    router.get("/v1/products/:id").handler(this::getProductById);

    return router;
  }

  public void middlewareHandler(RoutingContext routingContext) {
    String authToken = routingContext.request().headers().get("AuthToken");

    if (true || authToken != null && authToken.equals("123")) {
      LOGGER.info("Passed auth");
      // set CORS headers
      routingContext
          .response()
          .putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
          .putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE");
      routingContext.next();
    }
    else {
      LOGGER.info("Failed auth");
      routingContext
          .response()
          .setStatusCode(401)
          .putHeader("content-type", "application/json")
          .end(
              Json.encodePrettily(new JsonObject().put("error", "Not authorized"))
          );
    }
  }

  public void getProducts(RoutingContext routingContext) {
    vertx
        .eventBus()
        .send(
            "com.clooker.vertx.database.messages",
            new JsonObject().put("cmd", "getProducts"),
            reply -> routingContext
                .response()
                .setStatusCode(200)
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(reply.result().body()))
    );
  }

  public void getProductById(RoutingContext routingContext) {
    vertx
        .eventBus()
        .send(
            "com.clooker.vertx.database.messages",
            new JsonObject().put("cmd", "getProductById").put("id", routingContext.request().getParam("id")),
            reply -> routingContext
                .response()
                .setStatusCode(200)
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(reply.result().body()))
        );
  }
}