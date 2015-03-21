package io.turtle.metrics.impl;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import io.turtle.metrics.DropwizardMetrics;
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
    public void dec() {
        internalCounter.dec();
    }

    @Override
    public void register(String counterName) {
        internalCounter = metrics.counter(counterName);


    }
}
