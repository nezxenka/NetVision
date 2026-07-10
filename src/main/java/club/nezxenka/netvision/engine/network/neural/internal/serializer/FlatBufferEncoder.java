package club.nezxenka.netvision.engine.network.neural.serializer;

import club.nezxenka.netvision.engine.model.TickSample;
import club.nezxenka.netvision.serialize.model.TickDataFB;
import club.nezxenka.netvision.serialize.model.TickDataSequenceFB;
import com.google.flatbuffers.FlatBufferBuilder;
import java.util.List;

public class FlatBufferEncoder {
  private static final ThreadLocal<FlatBufferBuilder> BUILDER =
      ThreadLocal.withInitial(() -> new FlatBufferBuilder(1024));

  public byte[] encode(List<TickSample> data) {
    FlatBufferBuilder builder = BUILDER.get();
    builder.clear();
    int[] tickOffsets = new int[data.size()];
    for (int i = 0; i < data.size(); i++) {
      TickSample td = data.get(i);
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
