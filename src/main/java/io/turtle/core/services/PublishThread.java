package io.turtle.core.services;

import io.turtle.core.routing.RoutingMessage;
import io.turtle.core.routing.dispatching.Dispatching;
import io.turtle.core.routing.dispatching.impl.RoundRobin;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by gabriele on 09/03/15.
 */
public class PublishThread extends TurtleThread {

    private static final Logger log = Logger.getLogger(PublishThread.class.getName());

    private Resources resources;
    private Dispatching dispatching = new RoundRobin();

    public PublishThread(Resources resources) {
        this.resources = resources;
    }

    private BlockingQueue<RoutingMessage> messages = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<RoutingMessage> cache = new LinkedBlockingQueue<RoutingMessage>();

    public void addMessage(RoutingMessage routingMessage) throws InterruptedException {
        cache.add(routingMessage);
    }



    @Override
    public void run() {
        log.info("PublishThread started");
        while ((!this.isInterrupted()) && (!markToBeRemoved)) {
            try {
                cache.drainTo(messages);
                RoutingMessage routingMessage = messages.poll(200, TimeUnit.MILLISECONDS);
                if (routingMessage != null) {
                    resources.getSubscribeThreads().get(dispatchInteger).HandleRoutingMessage(routingMessage);
                    dispatchInteger = dispatching.getNextId(dispatchInteger,resources.getSubscribeThreads().size());
                }
            } catch (InterruptedException e) {
                if (!this.isAlive())
                    break;
                else e.printStackTrace();
            }

        }

    }
}


