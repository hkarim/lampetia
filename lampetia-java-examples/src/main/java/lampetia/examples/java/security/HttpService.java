package lampetia.examples.java.security;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

/**
 * @author Hossam Karim
 */

public class HttpService extends AbstractVerticle {

  public static final Logger logger = LoggerFactory.getLogger("HttpService");


  @Override
  public void start() {
    VertxGroupService service = new VertxGroupService(vertx);
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);

    // GET /api/1.0/group/:id
    router
      .get("/api/1.0/group/:id")
      .handler(context -> {
        String id = context.request().getParam("id");
        HttpServerResponse response = context.response();
        service.findOne(id, json -> {
          if (json.isPresent()) {
            response
              .putHeader("content-type", "application/json")
              .end(json.get().encode());
          } else {
            response
              .setStatusCode(404);
            response.end();
          }
        });
      });

    // GET /api/1.0/group
    router
      .get("/api/1.0/group")
      .handler(context -> {
        HttpServerResponse response = context.response();
        service.findAll(
          stream -> {
            JsonArray result = stream.reduce(new JsonArray(), JsonArray::add, JsonArray::addAll);
            response
              .putHeader("content-type", "application/json")
              .end(result.encode());
          });
      });


    server.requestHandler(router::accept).listen(4000);
    logger.info("Started");
  }

  @Override
  public void stop() throws Exception {
    logger.info("Stopped");
  }
}
