package lampetia.examples.java.security;

import io.vertx.core.json.JsonObject;

/**
 * @author Hossam Karim
 */
public interface SecurityModel {

  default JsonObject mapGroup(JsonObject json) {
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
}
