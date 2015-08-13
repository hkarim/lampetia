package lampetia.examples.java.security;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

/**
 * @author Hossam Karim
 */
public class SecurityService {

  public static void main(String...args) {

    Vertx vertx = Vertx.vertx();

    DeploymentOptions options = new DeploymentOptions();
    vertx.deployVerticle("lampetia.examples.java.security.HttpService", options);

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        vertx.close(result -> ServiceConfiguration.dataSource.close());
      }
    });
  }

}
