package lampetia.examples.java.security;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;


/**
 * @author Hossam Karim
 */

public class VertxGroupService implements SecurityModel {

  public static final Logger logger = LoggerFactory.getLogger("VertxGroupService");

  private QueryHelper query;

  public VertxGroupService(Vertx vertx) {
    JDBCClient client = JDBCClient.create(vertx, ServiceConfiguration.dataSource);
    query = new QueryHelper(client);
  }

  public void createGroup(JsonObject json, Handler<Optional<String>> handler) {
    String id = UUID.randomUUID().toString();
    String code = json.getString("code");
    String owner = json.getString("owner");
    query.u(
      "insert into lampetia.security_group(id, owner, code) values(?, ?, ?)",
      new JsonArray().add(id).add(owner).add(code),
      (i) -> {
        if (i == 1)
          handler.handle(Optional.of(id));
        else
          handler.handle(Optional.<String>empty());
      }
    );
  }

  public void findOne(String id, Handler<Optional<JsonObject>> handler) {
    query.q("select * from lampetia.security_group where id = ?", new JsonArray().add(id),
      (rs) -> {
        //rs.getRows().forEach(r -> logger.debug(r.encodePrettily()));
        Stream<JsonObject> groupStream = rs.getRows().stream().map(this::mapGroup);
        handler.handle(groupStream.findFirst());
      });
  }

  public void findAll(Handler<Stream<JsonObject>> handler) {
    query.q("select * from lampetia.security_group", (rs) -> {
      Stream<JsonObject> groupStream = rs.getRows().stream().map(this::mapGroup);
      handler.handle(groupStream);
    });
  }


}
