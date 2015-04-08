package io.turtle.env;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import io.turtle.configuration.impl.DefaultConfiguration;
import io.turtle.core.routing.RoutingMessage;
import io.turtle.core.services.Resources;
import io.turtle.core.services.SubscribeThread;
import io.turtle.core.tag.TagIndex;
import io.turtle.metrics.impl.DropwizardMetrics;
import io.turtle.pubsub.Subscriber;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by gabriele on 04/04/15.
 */
public abstract class TurtleEnvironment<T> implements IPublisher {


    private static final Logger log = Logger.getLogger(TurtleEnvironment.class.getName());
    final JmxReporter reporter = JmxReporter.forRegistry(DropwizardMetrics.getInstance().getTurtleMetrics()).inDomain("TurteMT").
            build();

    protected Resources resources = Resources.getInstance();


    protected volatile boolean isActive = false;

    protected ConcurrentHashMap<T, Subscriber> subscribers = new ConcurrentHashMap<>();
    protected TagIndex<T> tagIndex;

    protected SubscribeThread<T> subscribeThread;

    public synchronized void open() {
        log.info("Turtle Environment opening...");
        resources.init(new DefaultConfiguration());
        subscribeThread = new SubscribeThread(tagIndex, subscribers);
        resources.getServiceThread().submit(subscribeThread);
        reporter.start();
        isActive = true;
        log.info("Turtle Environment open done");
    }


    public synchronized void close() throws IOException {
        log.info("Turtle Environment closing... ");
        subscribers.entrySet().forEach(x -> unSubscribe(x.getKey()));

        resources.deInit();
        reporter.stop();
        isActive = false;
        log.info("Turtle Environment close done");

    }

    public MetricRegistry getMetrics() {
        return DropwizardMetrics.getInstance().getTurtleMetrics();
    }





    public synchronized T subscribe(Subscriber subscriber, String... tags) {

        for (String itm : tags) {
            subscriber.getTags().addTag(itm);
        }
        subscribers.put((T) subscriber.getSubscribeId(), subscriber);
        subscriber.getTags().getTags().forEach(x -> tagIndex.addTagToIndex(x, (T) subscriber.getSubscribeId()));
        return (T) subscriber.getSubscribeId();
    }


    public synchronized void unSubscribe(T subscriberId) {
        Subscriber subscriber = subscribers.get(subscriberId);
        subscriber.getTags().getTags().forEach(x -> tagIndex.removeTagToIndex(x, subscriberId));
        subscribers.remove(subscriberId);

    }


    @Override
    public void publish(Map<String, String> header, byte[] body, String... tags) throws InterruptedException, IOException {
        subscribeThread.HandleRoutingMessage(new RoutingMessage(header, body, tags));
    }

    @Override
    public void publish(RoutingMessage routingMessage) throws InterruptedException {
        subscribeThread.HandleRoutingMessage(routingMessage);
    }

    @Override
    public void publish(byte[] body, String... tags) throws InterruptedException, IOException {
        this.publish(null, body, tags);
    }

    public int getTagIndexCount() {
        return tagIndex.getCount();
    }

    public int getSubscribersCount() {
        return this.subscribers.size();
    }


}
