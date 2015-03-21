package io.turtle.env;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import io.turtle.configuration.impl.DefaultConfiguration;
import io.turtle.core.handlers.MessagesHandler;
import io.turtle.core.routing.Proxy;
import io.turtle.core.routing.RoutingMessage;
import io.turtle.core.services.Resources;
import io.turtle.metrics.DropwizardMetrics;
import io.turtle.pubsub.Subscriber;
import io.turtle.pubsub.impl.LocalSubscriber;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by gabriele on 09/03/2015.
 */
public class TurtleEnvironment {



    private static final Logger log = Logger.getLogger(TurtleEnvironment.class.getName());
    final JmxReporter reporter = JmxReporter.forRegistry(DropwizardMetrics.getInstance().getTurtleMetrics()).build();


    private ConcurrentHashMap<String, Subscriber> subscribers = new ConcurrentHashMap<>();
    private Resources resources = new Resources();
    private Proxy proxy = new Proxy(resources);

    private volatile boolean isActive = false;

    public synchronized void init() {
        resources.init(new DefaultConfiguration());

        proxy.init();
        reporter.start();
        isActive = true;
    }

    public synchronized void deInit() {
        proxy.deInit();
        resources.deInit();
        reporter.stop();
        isActive = false;

    }

    public void publish(Map<String, String> header, byte[] body, String... tags) throws InterruptedException {
        proxy.dispatchPublish(new RoutingMessage(header, body, tags));
    }

    public void publish(byte[] body, String... tags) throws InterruptedException {
        this.publish(null, body, tags);
    }

    public synchronized String subscribe(MessagesHandler messageHandler, String... tags) {
        return resources.registerSubscriber(new LocalSubscriber(), messageHandler, tags);
    }


    public synchronized void unSubscribe(String subscribeId) {
        resources.unRegisterSubscriber(subscribeId);
    }

    public MetricRegistry getMetrics() {
        return DropwizardMetrics.getInstance().getTurtleMetrics();
    }


    public int getTagIndexCount(){
        return  resources.getTagIndex().getCount();
    }

    public int getSubscribersCount(){

        return resources.getSubscribers().size();

    }
}
