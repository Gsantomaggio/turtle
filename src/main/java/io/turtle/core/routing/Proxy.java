package io.turtle.core.routing;

import io.turtle.core.services.PublishThread;
import io.turtle.core.services.Resources;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by gabriele on 09/03/2015.
 */
public class Proxy {

    final Resources resources;


    int threadCount = 2;

    List<PublishThread> publishs;
    public Proxy(Resources resources) {
        this.resources = resources;
        publishs = resources.getPublishThreads();

    }

    public void init() {

    }

    public void deInit() {

    }

    private AtomicInteger dispatchInteger = new AtomicInteger();


    public synchronized void dispatchPublish(RoutingMessage routingMessage) throws InterruptedException {
        publishs.get(dispatchInteger.get()).
                    addMessage(routingMessage);
            if (dispatchInteger.addAndGet(1) >= resources.getPublishThreads().size()) {
                dispatchInteger.set(0);
            }

    }


}
