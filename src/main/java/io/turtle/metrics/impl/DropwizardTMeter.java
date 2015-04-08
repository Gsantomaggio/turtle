package io.turtle.metrics.impl;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import io.turtle.metrics.TMeter;

/**
 * Created by gabriele on 30/03/2015.
 */
public class DropwizardTMeter implements TMeter {
    private Meter internalMeter;
    private MetricRegistry metrics = DropwizardMetrics.getInstance().getTurtleMetrics();

    @Override
    public void mark() {
        internalMeter.mark();

    }

    @Override
    public void register(String meterName) {
        internalMeter = metrics.meter(meterName);
    }
}
