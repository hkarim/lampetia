package lampetia.examples.java.security;

import com.zaxxer.hikari.HikariDataSource;

/**
 * @author Hossam Karim
 */
public abstract class ServiceConfiguration {

  private static HikariDataSource createDataSource() {
    HikariDataSource ds = new HikariDataSource();
    ds.setMaximumPoolSize(8);
    ds.setLeakDetectionThreshold(2000);
    ds.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
    ds.addDataSourceProperty("serverName", "localhost");
    ds.addDataSourceProperty("portNumber", 5432);
    ds.addDataSourceProperty("databaseName", "nxt");
    ds.addDataSourceProperty("user", "admin");
    ds.addDataSourceProperty("password", "admin");
    return ds;
  }

  public static final HikariDataSource dataSource = createDataSource();

}
