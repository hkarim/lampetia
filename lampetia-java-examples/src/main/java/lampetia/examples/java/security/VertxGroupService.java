package lampetia.examples.java.security;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;


/**
 * @author Hossam Karim
 */

public class VertxGroupService implements SecurityModel {

  public static final Logger logger = LoggerFactory.getLogger("VertxGroupService");

  private JDBCClient client;

  public VertxGroupService(Vertx vertx) {
    this.client = JDBCClient.create(vertx, ServiceConfiguration.dataSource);
  }


  public void findOne(String id, Handler<Optional<JsonObject>> handler) {
    client.getConnection(result -> {
      if (result.failed()) {
        handler.handle(Optional.empty());
      } else {
        SQLConnection connection = result.result();
        connection.queryWithParams(
          "select * from lampetia.security_group where id = ?",
          new JsonArray().add(id),
          outcome -> {
            if (outcome.failed()) {
              logger.fatal("findOne", outcome.cause());
              handler.handle(Optional.empty());
            } else {
              ResultSet resultSet = outcome.result();
              Stream<JsonObject> groupStream =
                resultSet.getRows().stream().map(this::mapGroup);
              handler.handle(groupStream.findFirst());
            }
          }
        );
        connection.close();
      }
    });
  }

  public void findAll(Handler<Stream<JsonObject>> handler) {
    client.getConnection(result -> {
      if (result.failed()) {
        handler.handle(Collections.<JsonObject>emptyList().stream());
      } else {
        SQLConnection connection = result.result();
        connection.query(
          "select * from lampetia.security_group",
          outcome -> {
            if (outcome.failed()) {
              logger.fatal("findAll", outcome.cause());
              handler.handle(Collections.<JsonObject>emptyList().stream());
            } else {
              ResultSet resultSet = outcome.result();
              Stream<JsonObject> groupStream =
                resultSet.getRows().stream().map(this::mapGroup);
              handler.handle(groupStream);
            }
          }
        );
        connection.close();
      }
    });
  }


}
