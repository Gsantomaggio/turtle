package io.turtle.env;

import io.turtle.configuration.impl.DefaultConfiguration;
import io.turtle.core.handlers.MessagesHandler;
import io.turtle.core.routing.Proxy;
import io.turtle.core.routing.RoutingMessage;
import io.turtle.core.services.Resources;
import io.turtle.jmx.JMXAgent;
import io.turtle.jmx.impl.ResourcesCounter;
import io.turtle.pubsub.Message;
import io.turtle.pubsub.Subscriber;
import io.turtle.pubsub.impl.LocalSubscriber;

import java.util.logging.Logger;

/**
 * Created by gabriele on 09/03/2015.
 */
public class TurtleEnvironment {

    private static final Logger log = Logger.getLogger(TurtleEnvironment.class.getName());

    Resources resources = new Resources();
    Proxy proxy = new Proxy(resources);
    JMXAgent jmxAgent = new JMXAgent(resources);

    public ResourcesCounter getResourcesCounter() {
        return jmxAgent.getResourcesCounter();
    }

    public void init() {
        resources.init(new DefaultConfiguration());
        proxy.init();
        jmxAgent.init();
    }

    public void deInit() {
        proxy.deInit();
        resources.deInit();
        jmxAgent.deInit();

    }

    public void publish(Message message, String... tags) throws InterruptedException {
        proxy.dispatchPublish(new RoutingMessage(message, tags));
    }


    public synchronized String subscribe(MessagesHandler<Message> messageHandler, String... tags) {
        Subscriber subscriber = new LocalSubscriber();
        subscriber.messageHandlers.add(messageHandler);
        for (String itm : tags) {
            subscriber.tags.addTag(itm);
        }
        resources.registerSubscriber(subscriber.subscriberID, subscriber);
        return subscriber.subscriberID;
    }


    public synchronized void unSubscribe(String subscribeId) {
        resources.unRegisterSubscriber(subscribeId);
    }
}
