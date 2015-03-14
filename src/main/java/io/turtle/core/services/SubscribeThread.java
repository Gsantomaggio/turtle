package io.turtle.core.services;

import io.turtle.core.routing.RoutingMessage;
import io.turtle.pubsub.Subscriber;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by gabriele on 08/03/15.
 */
public class SubscribeThread extends TurtleThread {


    private static final Logger log = Logger.getLogger(SubscribeThread.class.getName());

    private BlockingQueue<RoutingMessage> messages;

    Resources resources;

    public SubscribeThread(Resources resources) {
        messages = new LinkedBlockingQueue<>();
        this.resources = resources;
    }

    LinkedBlockingQueue<RoutingMessage> cache = new LinkedBlockingQueue<RoutingMessage>();
    public void HandleRoutingMessage(RoutingMessage routingMessage) throws InterruptedException {
        cache.put(routingMessage);
    }


    @Override
    public void run() {
        while ((!this.isInterrupted()) && (!markToBeRemoved)) {
            try {
                cache.drainTo(messages);

                // wait for message
                RoutingMessage routingMessage = messages.poll(200, TimeUnit.MILLISECONDS);
                if (routingMessage != null) {

                    // need this list in case one subscriber has more than one
                    // tag that match with the routingMessage.getTags()
                    // es: "pizza","pasta" and the tag is "pizza" the message must be send only one time.
                    ArrayList<String> tmpAlreadySent = new ArrayList<>();
                    routingMessage.getTags().forEach(tag -> {
                        // for each tag it will find the subscribersID to the indexMap
                        ArrayList<String> subList = resources.getSubscriberIdsbyTag(tag);
                        if (subList != null) {
                            for (String subscribeID : subList) {
                                Subscriber sub = resources.getSubscribers().get(subscribeID);
                                if (tmpAlreadySent.indexOf(subscribeID) < 0) {
                                    // if the message has not dispatched to the consumer with subscribe-id = subscribeID
                                    sub.messageHandlers.forEach(x ->
                                            {
                                                resources.getWorkerServiceThread().submit(() -> {
                                                            try {
                                                                x.handlerMessage(routingMessage.getHeader(),routingMessage.getBody(),tag);
                                                                resources.totalMessagesDeliveredByWorker.addAndGet(1);
                                                            } catch (Exception e) {
                                                                log.severe("error handlerMessage:" + e);
                                                                // must add a error handler
                                                            }
                                                        }
                                                );
                                            }
                                    );
                                    tmpAlreadySent.add(subscribeID);
                                }
                            }
                        } /// else that means the tag has not subscriber

                    });
                    tmpAlreadySent.clear();
                    resources.incMessagesDelivered();
                }


            } catch (InterruptedException e) {
                if (!this.isAlive())
                    break;

            }
        }
    }

}
