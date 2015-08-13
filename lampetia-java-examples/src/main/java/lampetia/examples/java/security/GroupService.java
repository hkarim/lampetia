package lampetia.examples.java.security;

import com.zaxxer.hikari.HikariDataSource;
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

public class GroupService {

  public static final Logger logger = LoggerFactory.getLogger("GroupService");

  private JDBCClient client;

  public GroupService(Vertx vertx) {
    HikariDataSource ds = new HikariDataSource();
    ds.setMaximumPoolSize(8);
    ds.setLeakDetectionThreshold(2000);
    ds.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
    ds.addDataSourceProperty("serverName", "localhost");
    ds.addDataSourceProperty("portNumber", 5432);
    ds.addDataSourceProperty("databaseName", "nxt");
    ds.addDataSourceProperty("user", "admin");
    ds.addDataSourceProperty("password", "admin");
    this.client = JDBCClient.create(vertx, ds);
  }

  private JsonObject mapGroup(JsonObject json) {
    return
      new JsonObject()
        .put("id", json.getString("id"))
        .put("data",
          new JsonObject()
            .put("code", json.getString("code")))
        .put("ref",
          new JsonObject()
            .put("owner", json.getString("owner")));
  }

  public void findOne(String id, Handler<Optional<JsonObject>> handler) {
    client.getConnection(result -> {
      if(result.failed()) {
        handler.handle(Optional.empty());
      } else {
        SQLConnection connection = result.result();
        connection.queryWithParams(
          "select * from lampetia.security_group where id = ?",
          new JsonArray().add(id),
          outcome -> {
            if(outcome.failed()) {
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
      if(result.failed()) {
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
