package io.turtle.core.routing;

import io.turtle.core.services.Resources;

/**
 * Created by gabriele on 09/03/2015.
 */
public class Proxy {

    final Resources resources;


    int threadCount = 2;

    public Proxy(Resources resources) {
        this.resources = resources;

    }

    public void init() {

    }

    public void deInit() {

    }

    private int dispatchInteger = 0;
    private int total = 0;


    public void dispatchPublish(RoutingMessage routingMessage) throws InterruptedException {
        resources.getPublishThreads().get(dispatchInteger).addMessage(routingMessage);
        total = resources.getPublishThreads().size();
        dispatchInteger += 1;
        if (dispatchInteger >= total) {
            dispatchInteger = 0;
        }
    }


}
