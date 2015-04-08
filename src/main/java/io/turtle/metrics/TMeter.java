package io.turtle.metrics;

/**
 * Created by gabriele on 30/03/2015.
 */
public interface TMeter {

    void mark();
    void register(String meterName);
}
