package io.turtle.metrics;

/**
 * Created by gabriele on 20/03/15.
 */
public interface TCounter {
    void inc();

    void inc(long value);

    void dec();

    void dec(long value);

    void register(String counterName);
    long getCount();
}
