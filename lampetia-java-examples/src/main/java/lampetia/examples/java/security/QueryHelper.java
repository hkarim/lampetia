package lampetia.examples.java.security;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import lampetia.sql.ast.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hossam Karim
 */

public class QueryHelper {

  private static final Logger logger = LoggerFactory.getLogger("QueryHelper");

  private JDBCClient client;

  public QueryHelper(JDBCClient client) {
    this.client = client;
  }

  public void q(String sql, Handler<ResultSet> handler) {
    client.getConnection(connResult -> {
      if (connResult.failed()) {
        logger.error("Connection Failed", connResult.cause());
      } else {
        SQLConnection connection = connResult.result();
        logger.debug("Connection acquired");
        connection.query(sql, (outcome) -> {
          if (outcome.failed()) {
            logger.error("ResultSet Failed", outcome.cause());
          } else {
            ResultSet rs = outcome.result();
            handler.handle(rs);
          }
        });
        logger.debug("Closing connection");
        connection.close();
      }
    });
  }

  public void q(String sql, JsonArray parameters, Handler<ResultSet> handler) {
    client.getConnection(connResult -> {
      if (connResult.failed()) {
        logger.error("Connection Failed", connResult.cause());
      } else {
        SQLConnection connection = connResult.result();
        logger.debug("Connection acquired");
        connection.queryWithParams(sql, parameters, (outcome) -> {
          if (outcome.failed()) {
            logger.error("ResultSet Failed", outcome.cause());
          } else {
            ResultSet rs = outcome.result();
            handler.handle(rs);
          }
        });
        logger.debug("Closing connection");
        connection.close();
      }
    });
  }

  public void u(String sql, JsonArray parameters, Handler<Integer> handler) {
    client.getConnection(connResult -> {
      if (connResult.failed()) {
        logger.error("Connection Failed", connResult.cause());
      } else {
        SQLConnection connection = connResult.result();
        logger.debug("Connection acquired");
        connection.updateWithParams(sql, parameters, (outcome) -> {
          if (outcome.failed()) {
            logger.error("ResultSet Failed", outcome.cause());
          } else {
            UpdateResult urs = outcome.result();
            handler.handle(urs.getUpdated());
          }
        });
        logger.debug("Closing connection");
        connection.close();
      }
    });
  }

}
