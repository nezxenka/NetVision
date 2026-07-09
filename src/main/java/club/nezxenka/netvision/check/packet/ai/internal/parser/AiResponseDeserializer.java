package club.nezxenka.netvision.check.packet.ai.internal.parser;

import club.nezxenka.netvision.server.model.AIResponse;
import com.google.gson.Gson;

public class AiResponseDeserializer {
  private static final Gson GSON = new Gson();

  public AIResponse deserialize(String json) {
    return GSON.fromJson(json, AIResponse.class);
  }
}
