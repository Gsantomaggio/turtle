package io.turtle.jmx.impl;

import io.turtle.core.services.Resources;
import io.turtle.jmx.ResourcesCounterMXBean;

/**
 * Created by gabriele on 10/03/15.
 */
public class ResourcesCounter implements ResourcesCounterMXBean {

    Resources resources;

    public ResourcesCounter(Resources resources) {
        this.resources = resources;

    }

    @Override
    public int getSubscriberCount() {
        return resources.getSubscribers().size();
    }

    @Override
    public int getTagIndexCount() {
        return resources.getTagIndex().size();
    }

    @Override
    public int getTotalMessagesDeliveredByWorker() {
        return resources.totalMessagesDeliveredByWorker.get();
    }


    @Override
    public int getTotalMessagesPublished() {
        return resources.totalMessagesPublished.get();
    }

    @Override
    public int getTotalMessagesDelivered() {
        return resources.totalMessagesDelivered.get();
    }

}
