package club.nezxenka.netvision.serialize.model;

import com.google.flatbuffers.BaseVector;
import com.google.flatbuffers.Constants;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class TickDataSequenceFB extends Table {

  public static void ValidateVersion() {
    Constants.FLATBUFFERS_25_2_10();
  }

  public static TickDataSequenceFB getRootAsTickDataSequence(ByteBuffer _bb) {
    return getRootAsTickDataSequence(_bb, new TickDataSequenceFB());
  }

  public static TickDataSequenceFB getRootAsTickDataSequence(
      ByteBuffer _bb, TickDataSequenceFB obj) {
    _bb.order(ByteOrder.LITTLE_ENDIAN);
    return obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb);
  }

  public void __init(int _i, ByteBuffer _bb) {
    __reset(_i, _bb);
  }

  public TickDataSequenceFB __assign(int _i, ByteBuffer _bb) {
    __init(_i, _bb);
    return this;
  }

  public TickDataFB ticks(int j) {
    return ticks(new TickDataFB(), j);
  }

  public TickDataFB ticks(TickDataFB obj, int j) {
    int o = __offset(4);
    return o != 0 ? obj.__assign(__indirect(__vector(o) + j * 4), bb) : null;
  }

  public int ticksLength() {
    int o = __offset(4);
    return o != 0 ? __vector_len(o) : 0;
  }

  public TickDataFB.Vector ticksVector() {
    return ticksVector(new TickDataFB.Vector());
  }

  public TickDataFB.Vector ticksVector(TickDataFB.Vector obj) {
    int o = __offset(4);
    return o != 0 ? obj.__assign(__vector(o), 4, bb) : null;
  }

  public static int createTickDataSequence(FlatBufferBuilder builder, int ticksOffset) {
    builder.startTable(1);
    TickDataSequenceFB.addTicks(builder, ticksOffset);
    return TickDataSequenceFB.endTickDataSequence(builder);
  }

  public static void startTickDataSequence(FlatBufferBuilder builder) {
    builder.startTable(1);
  }

  public static void addTicks(FlatBufferBuilder builder, int ticksOffset) {
    builder.addOffset(0, ticksOffset, 0);
  }

  public static int createTicksVector(FlatBufferBuilder builder, int[] data) {
    builder.startVector(4, data.length, 4);
    for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]);
    return builder.endVector();
  }

  public static void startTicksVector(FlatBufferBuilder builder, int numElems) {
    builder.startVector(4, numElems, 4);
  }

  public static int endTickDataSequence(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static void finishTickDataSequenceBuffer(FlatBufferBuilder builder, int offset) {
    builder.finish(offset);
  }

  public static void finishSizePrefixedTickDataSequenceBuffer(
      FlatBufferBuilder builder, int offset) {
    builder.finishSizePrefixed(offset);
  }

  public static final class Vector extends BaseVector {

    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) {
      __reset(_vector, _element_size, _bb);
      return this;
    }

    public TickDataSequenceFB get(int j) {
      return get(new TickDataSequenceFB(), j);
    }

    public TickDataSequenceFB get(TickDataSequenceFB obj, int j) {
      return obj.__assign(__indirect(__element(j), bb), bb);
    }
  }
}
