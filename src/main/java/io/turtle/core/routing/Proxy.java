package io.turtle.core.routing;

import io.turtle.core.services.Resources;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by gabriele on 09/03/2015.
 */
public class Proxy {

    final Resources resources;



    public Proxy(Resources resources) {
        this.resources = resources;

    }

    public void init() {

    }

    public void deInit() {

    }

    private AtomicInteger dispatchInteger = new AtomicInteger();
    public synchronized void dispatchPublish(RoutingMessage routingMessage) throws InterruptedException {
        resources.getPublishThreads().get(dispatchInteger.get()).
                    addMessage(routingMessage);
            if (dispatchInteger.addAndGet(1) >= resources.getPublishThreads().size()) {
                dispatchInteger.set(0);
            }

    }


}
