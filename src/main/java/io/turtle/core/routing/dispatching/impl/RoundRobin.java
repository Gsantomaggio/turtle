package io.turtle.core.routing.dispatching.impl;

import io.turtle.core.routing.dispatching.Dispatching;

/**
 * Created by gabriele on 15/03/15.
 */
public class RoundRobin implements Dispatching {
    @Override
    public int getNextId(int current, int maxvalue) {
        current += 1;
        if (current >= maxvalue) {
            current = 0;
        }
        return current;
    }
}
