package io.turtle.core.services;

import com.codahale.metrics.MetricRegistry;
import io.turtle.core.routing.RoutingMessage;
import io.turtle.core.tag.TagIndex;
import io.turtle.metrics.TCounter;
import io.turtle.metrics.impl.DropwizardTCounter;
import io.turtle.pubsub.Subscriber;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by gabriele on 08/03/15.
 */
public class SubscribeThread<T> extends TurtleThread {

    private static final Logger log = Logger.getLogger(SubscribeThread.class.getName());
    private final ConcurrentHashMap<T, Subscriber> subscribers;

    private BlockingQueue<RoutingMessage> messages;

    private TCounter messagesDelivered;
    private TCounter workerDelivered;


    Resources resources;
    private TagIndex<T> tagIndex;

    public SubscribeThread(TagIndex<T> tagIndex,ConcurrentHashMap<T, Subscriber> subscribers) {
        messages = new LinkedBlockingQueue<>();
        this.resources = Resources.getInstance();
        this.tagIndex = tagIndex;
        this.subscribers = subscribers;
        messagesDelivered = new DropwizardTCounter();
        workerDelivered = new DropwizardTCounter();

        messagesDelivered.register(MetricRegistry.name("Subscriber", "messagesDelivered", ""));
        workerDelivered.register(MetricRegistry.name("Subscriber", "workerDelivered", ""));
    }

    LinkedBlockingQueue<RoutingMessage> cache = new LinkedBlockingQueue<RoutingMessage>();
    public void HandleRoutingMessage(RoutingMessage routingMessage) throws InterruptedException {
        cache.put(routingMessage);
    }


    @Override
    public void run() {
        log.info("SubscribeThread started");
        while ((!this.isInterrupted()) && (!markToBeRemoved)) {
            try {
                cache.drainTo(messages);

                // wait for message
                RoutingMessage routingMessage = messages.poll(200, TimeUnit.MILLISECONDS);
                if (routingMessage != null) {

                    // need this list in case one subscriber has more than one
                    // tag that match with the routingMessage.getTags()
                    // es: "pizza","pasta" and the tag is "pizza" the message must be send only one time.
                    ArrayList<T> tmpAlreadySent = new ArrayList<>();
                    routingMessage.getTags().forEach(tag -> {
                        // for each tag it will find the subscribersID to the indexMap
                        ArrayList<T> subList = tagIndex.getSubscriberIdsByTag(tag);
                        if (subList != null) {
                            for (T subscribeID : subList) {
                                Subscriber<T> sub = this.subscribers.get(subscribeID);
                                if (tmpAlreadySent.indexOf(subscribeID) < 0) {
                                    // if the message has not dispatched to the consumer with subscribe-id = subscribeID
                                    sub.getMessageHandlers().forEach(x ->
                                                    resources.getWorkerServiceThread().submit(() -> {
                                                                try {
                                                                    synchronized (x) { // this synchronized guarantees one message at time for handle
                                                                        x.handleMessage(routingMessage.getHeader(), routingMessage.getBody(), tag,subscribeID);
                                                                    }
                                                                    workerDelivered.inc();
                                                                } catch (Exception e) {
                                                                    log.severe("error handlerMessage:" + e);
                                                                    // must add a error handler
                                                                }
                                                            }
                                                    )
                                    );
                                    tmpAlreadySent.add(subscribeID);
                                }
                            }
                        } /// else that means the tag has not subscriber

                    });
                    tmpAlreadySent.clear();
                    messagesDelivered.inc();
                }


            } catch (InterruptedException e) {
                if (!this.isAlive())
                    break;

            }
        }
    }

}
