package com.clooker.vertx.formalapiexample.resources;

import com.clooker.vertx.formalapiexample.entity.Product;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.ArrayList;
import java.util.List;

public class ProductResources {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProductResources.class);

  public Router getRouter(Vertx vertx) {
    Router router = Router.router(vertx);
    router.route("/*").handler(this::middlewareHandler);

    // BodyHandler converts http req body into JSON
    router.route("/v1/products*").handler(BodyHandler.create());

    router.get("/v1/products").handler(this::getAllProducts);
    router.get("/v1/products/:id").handler(this::getProductById);
    router.post("/v1/products").handler(this::addProduct);
    router.put("/v1/products/:id").handler(this::updateProductById);
    router.delete("/v1/products/:id").handler(this::deleteProductById);

    return router;
  }

  public void middlewareHandler(RoutingContext routingContext) {
    String authToken = routingContext.request().headers().get("AuthToken");

    if (authToken != null && authToken.equals("123")) {
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

  public void addProduct(RoutingContext routingContext) {
    JsonObject bodyJson  = routingContext.getBodyAsJson();
    String description = bodyJson.getString("description");
    int number = bodyJson.getInteger("number");
    Product product = new Product(description, "", number);

    // put product into db and use the id that is returned
    product.setId("239482");

    routingContext
        .response()
        .setStatusCode(201) // 201 indicates created resource
        .putHeader("content-type", "application/json")
        .end(Json.encodePrettily(product));
  }

  public void deleteProductById(RoutingContext routingContext) {
    final String productId = routingContext.request().getParam("id");
    routingContext
        .response()
        .setStatusCode(200)
        .putHeader("content-type", "application/json")
        .end();
  }

  public void getAllProducts(RoutingContext routingContext) {
    Product product1234 = new Product("item 1234", "1234", 1);
    Product product2345 = new Product("item 2345", "2345", 2);
    List<Product> products = new ArrayList<>();
    products.add(product1234);
    products.add(product2345);
    JsonObject response = new JsonObject().put("products", products);

    routingContext
        .response()
        .setStatusCode(200)
        .putHeader("content-type", "application/json")
        .end(Json.encodePrettily(response));
  }

  public void getProductById(RoutingContext routingContext) {
    final String id = routingContext.request().getParam("id");
    Product product = new Product("My item " + id, id, 5);
    routingContext
        .response()
        .setStatusCode(200)
        .putHeader("content-type", "application/json")
        .end(Json.encodePrettily(product));
  }

  public void updateProductById(RoutingContext routingContext) {
    final String id = routingContext.request().getParam("id");
    JsonObject bodyJson  = routingContext.getBodyAsJson();
    String description = bodyJson.getString("description");
    int number = bodyJson.getInteger("number");
    Product product = new Product(description, id, number);
    routingContext
        .response()
        .setStatusCode(200)
        .putHeader("content-type", "application/json")
        .end(Json.encodePrettily(product));
  }
}
