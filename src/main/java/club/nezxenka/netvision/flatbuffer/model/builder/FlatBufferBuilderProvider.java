package club.nezxenka.netvision.flatbuffer.model.builder;

import com.google.flatbuffers.FlatBufferBuilder;

public class FlatBufferBuilderProvider {
  private static final ThreadLocal<FlatBufferBuilder> BUILDER =
      ThreadLocal.withInitial(() -> new FlatBufferBuilder(1024));

  public FlatBufferBuilder acquire() {
    return BUILDER.get();
  }

  public void reset(FlatBufferBuilder builder) {
    builder.clear();
  }
}
