package com.clooker.vertx.finalapi;

import com.clooker.vertx.finalapi.database.MongoManager;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

public class MongoVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoVerticle.class);
  private static MongoClient mongoClient = null;

  public static void main(String[] args) {
    Vertx.clusteredVertx(new VertxOptions().setClustered(true), results -> {
      if (results.succeeded()) {
        Vertx vertx = results.result();
        ConfigRetriever.create(vertx).getConfig(config -> {
          if (config.succeeded()) {
            JsonObject configJson = config.result();
            DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(configJson);
            vertx.deployVerticle(MongoVerticle.class, deploymentOptions);
          }
        });
      }
    });
  }

  @Override
  public void start() {
    LOGGER.info("mongoVerticle started");
    mongoClient = MongoClient.createShared(
        vertx,
        new JsonObject()
            .put("connection_string", config().getString("MONGODB.CONNECTION_STRING"))
            .put("useObjectId", true)
    );
    MongoManager mongoManager = new MongoManager(mongoClient);
    mongoManager.registerConsumer(vertx);
  }

  @Override
  public void stop() {
    LOGGER.info("mongoVerticle stopped");
  }
}
