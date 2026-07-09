package club.nezxenka.netvision.check.packet.ai.internal.serializer;

import club.nezxenka.netvision.data.TickData;
import club.nezxenka.netvision.flatbuffer.model.TickDataFB;
import club.nezxenka.netvision.flatbuffer.model.TickDataSequenceFB;
import com.google.flatbuffers.FlatBufferBuilder;
import java.util.List;

public class FlatBufferEncoder {
  private static final ThreadLocal<FlatBufferBuilder> BUILDER =
      ThreadLocal.withInitial(() -> new FlatBufferBuilder(1024));

  public byte[] encode(List<TickData> data) {
    FlatBufferBuilder builder = BUILDER.get();
    builder.clear();
    int[] tickOffsets = new int[data.size()];
    for (int i = 0; i < data.size(); i++) {
      TickData td = data.get(i);
      tickOffsets[i] =
          TickDataFB.createTickData(
              builder,
              td.deltaYaw,
              td.deltaPitch,
              td.accelYaw,
              td.accelPitch,
              td.jerkPitch,
              td.jerkYaw,
              td.gcdErrorYaw,
              td.gcdErrorPitch);
    }
    int ticksVector = TickDataSequenceFB.createTicksVector(builder, tickOffsets);
    int root = TickDataSequenceFB.createTickDataSequence(builder, ticksVector);
    TickDataSequenceFB.finishTickDataSequenceBuffer(builder, root);
    return builder.sizedByteArray();
  }
}
