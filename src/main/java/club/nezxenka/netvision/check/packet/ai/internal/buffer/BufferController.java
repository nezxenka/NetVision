package club.nezxenka.netvision.check.packet.ai.internal.buffer;

public class BufferController {
  private double buffer;
  private final double flag;
  private final double resetOnFlag;
  private final double multiplier;
  private final double decrease;

  public BufferController(double flag, double resetOnFlag, double multiplier, double decrease) {
    this.flag = flag;
    this.resetOnFlag = resetOnFlag;
    this.multiplier = multiplier;
    this.decrease = decrease;
  }

  public void increase(double probability) {
    buffer = Math.min(buffer + (probability * multiplier), flag);
  }

  public boolean isAboveFlag() {
    return buffer >= flag;
  }

  public void reset() {
    buffer = Math.max(0, buffer - resetOnFlag);
  }

  public void decay() {
    buffer = Math.max(0, buffer - decrease);
  }

  public double get() {
    return buffer;
  }
}
