package io.turtle.metrics.impl;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import io.turtle.metrics.TCounter;

/**
 * Created by gabriele on 20/03/15.
 */
public class DropwizardTCounter implements TCounter {


    private Counter internalCounter;
    private MetricRegistry metrics = DropwizardMetrics.getInstance().getTurtleMetrics();

    @Override
    public void inc() {
        internalCounter.inc();

    }

    @Override
    public void inc(long value) {
        internalCounter.inc(value);
    }

    @Override
    public void dec() {
        internalCounter.dec();
    }

    @Override
    public void dec(long value) {
        internalCounter.dec(value);
    }

    @Override
    public void register(String counterName) {
        internalCounter = metrics.counter(counterName);


    }

    @Override
    public long getCount() {
        return internalCounter.getCount();
    }
}
