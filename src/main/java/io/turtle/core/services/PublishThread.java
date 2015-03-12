package io.turtle.core.services;

import io.turtle.core.routing.RoutingMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by gabriele on 09/03/15.
 */
public class PublishThread extends TurtleThread {


    private Resources resources;

    public PublishThread(Resources resources) {
        this.resources = resources;
    }

    private BlockingQueue<RoutingMessage> messages = new LinkedBlockingQueue<>();

    LinkedBlockingQueue<RoutingMessage> cache = new LinkedBlockingQueue<RoutingMessage>();

    public void addMessage(RoutingMessage routingMessage) throws InterruptedException {
        cache.add(routingMessage);
    }

    private int nextThread = 0;

    @Override
    public void run() {
        while ((!this.isInterrupted()) && (!markToBeRemoved)) {
            try {

                cache.drainTo(messages);

                RoutingMessage routingMessage = messages.poll(200, TimeUnit.MILLISECONDS);

                if (routingMessage != null) {
                    resources.getSubscribeThreads().get(nextThread).HandleRoutingMessage(routingMessage);
                    resources.incMessagesPublished();
                    nextThread += 1;
                    if (nextThread >= resources.getSubscribeThreads().size()) {
                        nextThread = 0;
                    }


                }
            } catch (InterruptedException e) {
                if (!this.isAlive())
                    break;
                else e.printStackTrace();
            }

        }

    }
}


