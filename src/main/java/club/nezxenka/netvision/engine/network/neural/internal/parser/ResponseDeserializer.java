package club.nezxenka.netvision.engine.network.neural.parser;

import club.nezxenka.netvision.remote.model.AIResponse;
import com.google.gson.Gson;

public class ResponseDeserializer {
  private static final Gson GSON = new Gson();

  public AIResponse deserialize(String json) {
    return GSON.fromJson(json, AIResponse.class);
  }
}
