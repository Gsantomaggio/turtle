package io.turtle.metrics.impl;

import com.codahale.metrics.MetricRegistry;


/**
 * Created by gabriele on 20/03/15.
 */
public class DropwizardMetrics  {

    private static final MetricRegistry turtleMetrics = new MetricRegistry();

    private static DropwizardMetrics instance = null;
    protected DropwizardMetrics() {
        // Exists only to defeat instantiation.
    }
    public static DropwizardMetrics getInstance() {
        if(instance == null) {
            instance = new DropwizardMetrics();
        }
        return instance;
    }

    public MetricRegistry getTurtleMetrics(){
        return turtleMetrics;

    }


}
