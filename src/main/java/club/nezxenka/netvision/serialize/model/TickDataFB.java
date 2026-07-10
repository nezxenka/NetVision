package club.nezxenka.netvision.serialize.model;

import com.google.flatbuffers.BaseVector;
import com.google.flatbuffers.Constants;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class TickDataFB extends Table {

  public static void ValidateVersion() {
    Constants.FLATBUFFERS_25_2_10();
  }

  public static TickDataFB getRootAsTickData(ByteBuffer _bb) {
    return getRootAsTickData(_bb, new TickDataFB());
  }

  public static TickDataFB getRootAsTickData(ByteBuffer _bb, TickDataFB obj) {
    _bb.order(ByteOrder.LITTLE_ENDIAN);
    return obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb);
  }

  public void __init(int _i, ByteBuffer _bb) {
    __reset(_i, _bb);
  }

  public TickDataFB __assign(int _i, ByteBuffer _bb) {
    __init(_i, _bb);
    return this;
  }

  public float deltaYaw() {
    int o = __offset(4);
    return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f;
  }

  public float deltaPitch() {
    int o = __offset(6);
    return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f;
  }

  public float accelYaw() {
    int o = __offset(8);
    return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f;
  }

  public float accelPitch() {
    int o = __offset(10);
    return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f;
  }

  public float jerkPitch() {
    int o = __offset(12);
    return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f;
  }

  public float jerkYaw() {
    int o = __offset(14);
    return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f;
  }

  public float gcdErrorYaw() {
    int o = __offset(16);
    return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f;
  }

  public float gcdErrorPitch() {
    int o = __offset(18);
    return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f;
  }

  public static int createTickData(
      FlatBufferBuilder builder,
      float deltaYaw,
      float deltaPitch,
      float accelYaw,
      float accelPitch,
      float jerkPitch,
      float jerkYaw,
      float gcdErrorYaw,
      float gcdErrorPitch) {
    builder.startTable(8);
    TickDataFB.addGcdErrorPitch(builder, gcdErrorPitch);
    TickDataFB.addGcdErrorYaw(builder, gcdErrorYaw);
    TickDataFB.addJerkYaw(builder, jerkYaw);
    TickDataFB.addJerkPitch(builder, jerkPitch);
    TickDataFB.addAccelPitch(builder, accelPitch);
    TickDataFB.addAccelYaw(builder, accelYaw);
    TickDataFB.addDeltaPitch(builder, deltaPitch);
    TickDataFB.addDeltaYaw(builder, deltaYaw);
    return TickDataFB.endTickData(builder);
  }

  public static void startTickData(FlatBufferBuilder builder) {
    builder.startTable(8);
  }

  public static void addDeltaYaw(FlatBufferBuilder builder, float deltaYaw) {
    builder.addFloat(0, deltaYaw, 0.0f);
  }

  public static void addDeltaPitch(FlatBufferBuilder builder, float deltaPitch) {
    builder.addFloat(1, deltaPitch, 0.0f);
  }

  public static void addAccelYaw(FlatBufferBuilder builder, float accelYaw) {
    builder.addFloat(2, accelYaw, 0.0f);
  }

  public static void addAccelPitch(FlatBufferBuilder builder, float accelPitch) {
    builder.addFloat(3, accelPitch, 0.0f);
  }

  public static void addJerkPitch(FlatBufferBuilder builder, float jerkPitch) {
    builder.addFloat(4, jerkPitch, 0.0f);
  }

  public static void addJerkYaw(FlatBufferBuilder builder, float jerkYaw) {
    builder.addFloat(5, jerkYaw, 0.0f);
  }

  public static void addGcdErrorYaw(FlatBufferBuilder builder, float gcdErrorYaw) {
    builder.addFloat(6, gcdErrorYaw, 0.0f);
  }

  public static void addGcdErrorPitch(FlatBufferBuilder builder, float gcdErrorPitch) {
    builder.addFloat(7, gcdErrorPitch, 0.0f);
  }

  public static int endTickData(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {

    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) {
      __reset(_vector, _element_size, _bb);
      return this;
    }

    public TickDataFB get(int j) {
      return get(new TickDataFB(), j);
    }

    public TickDataFB get(TickDataFB obj, int j) {
      return obj.__assign(__indirect(__element(j), bb), bb);
    }
  }
}
