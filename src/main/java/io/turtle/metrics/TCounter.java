package io.turtle.metrics;

/**
 * Created by gabriele on 20/03/15.
 */
public interface TCounter {
  void inc();
  void dec();
  void register(String counterName);
}
