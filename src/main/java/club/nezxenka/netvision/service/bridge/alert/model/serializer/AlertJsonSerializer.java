package club.nezxenka.netvision.service.bridge.alert.model.serializer;

import club.nezxenka.netvision.service.bridge.alert.model.CrossServerAlert;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AlertJsonSerializer {
  private final ObjectMapper mapper;

  public AlertJsonSerializer(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  public String serialize(CrossServerAlert alert) throws JsonProcessingException {
    return mapper.writeValueAsString(alert);
  }

  public CrossServerAlert deserialize(String json) throws JsonProcessingException {
    return mapper.readValue(json, CrossServerAlert.class);
  }
}
