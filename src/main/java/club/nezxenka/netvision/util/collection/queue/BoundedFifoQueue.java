package club.nezxenka.netvision.util.collection.queue;

import java.util.ArrayDeque;

public class BoundedFifoQueue<T> {
  private final ArrayDeque<T> deque = new ArrayDeque<>();
  private final int maxSize;

  public BoundedFifoQueue(int maxSize) {
    this.maxSize = maxSize;
  }

  public void add(T item) {
    deque.addLast(item);
    if (deque.size() > maxSize) deque.removeFirst();
  }

  public T poll() {
    return deque.pollFirst();
  }

  public T peek() {
    return deque.peekFirst();
  }

  public int size() {
    return deque.size();
  }

  public void clear() {
    deque.clear();
  }
}
