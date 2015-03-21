package io.turtle.core.services;

import io.turtle.configuration.Configuration;
import io.turtle.core.handlers.MessagesHandler;
import io.turtle.core.services.utils.TurtleThreadFactory;
import io.turtle.core.tag.TagIndex;
import io.turtle.core.tag.impl.LocalTagIndex;
import io.turtle.metrics.DropwizardMetrics;
import io.turtle.metrics.TCounter;
import io.turtle.metrics.impl.DropwizardTCounter;
import io.turtle.pubsub.Subscriber;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Created by gabriele on 08/03/15.
 * Resources is the  core class, contains threads
 */
public class Resources{

    private static final Logger log = Logger.getLogger(Resources.class.getName());
    private ExecutorService internalServiceThread = null;
    private ExecutorService workerServiceThread = null;
    private ConcurrentHashMap<String, Subscriber> subscribers = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Integer,SubscribeThread> subscribeThreads = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer,PublishThread> publishThreads = new ConcurrentHashMap<>();

    private Configuration currentConfiguration;
    private TagIndex tagIndex = new LocalTagIndex();
    public TagIndex getTagIndex(){
        return tagIndex;
    }


    public Map<Integer,PublishThread> getPublishThreads() {
        return publishThreads;
    }

    public Map<Integer,SubscribeThread> getSubscribeThreads() {
        return subscribeThreads;
    }

    public Map<String, Subscriber> getSubscribers() {
        return subscribers;
    }


    public Configuration getCurrentConfiguration() {
        return currentConfiguration;
    }


    public void init(Configuration configuration) {
        currentConfiguration = configuration;
        int threadCount = configuration.getDispatchThreadCount();
        log.info(" Init resources, core:" + threadCount + " - Subscribe Threads: " + configuration.getSubscribeThreadCount() + " - Publish Threads: " + configuration.getPublishThreadCount());
        internalServiceThread = Executors.newFixedThreadPool(threadCount, new TurtleThreadFactory("ServiceThread"));
        for (int i = 0; i < configuration.getSubscribeThreadCount(); i++) {
            SubscribeThread subscribeThread = new SubscribeThread(this);

            subscribeThreads.put(new Integer(i),subscribeThread);
            internalServiceThread.submit(subscribeThread);
        }

        for (int i = 0; i < configuration.getPublishThreadCount(); i++) {
            PublishThread publishThread = new PublishThread(this);
            publishThreads.put(new Integer(i), publishThread);
            internalServiceThread.submit(publishThread);
        }
        workerServiceThread = Executors.newFixedThreadPool(configuration.getWorkersThreadCount(), new TurtleThreadFactory("WorkerThread"));
    }


    public void addServiceThread(Thread thread) {
        internalServiceThread.submit(thread);
    }


    public ArrayList<String> getSubscriberIdsbyTag(String tag) {
        return tagIndex.getSubscriberIdsByTag(tag);
    }


    public synchronized String registerSubscriber(Subscriber subscriber,MessagesHandler messageHandler,String... tags) {

        for (String itm : tags) {
            subscriber.tags.addTag(itm);
        }
        subscriber.messageHandlers.add(messageHandler);
        subscribers.put(subscriber.subscriberID, subscriber);
        subscriber.tags.getTags().forEach(x -> tagIndex.addTagToIndex(x, subscriber.subscriberID));
        return subscriber.subscriberID;
    }


    public synchronized void unRegisterSubscriber(String subscriberId) {
        Subscriber subscriber = subscribers.get(subscriberId);
        subscriber.tags.getTags().forEach(x -> {
           tagIndex.removeTagToIndex(x,subscriberId);
        });
        subscribers.remove(subscriberId);

    }


    public void deInit() {
        if (internalServiceThread != null) {
            log.info("De init resources");
            subscribers.entrySet().forEach(x->unRegisterSubscriber(x.getKey()));
            internalServiceThread.shutdownNow();
            workerServiceThread.shutdownNow();
            try {
                internalServiceThread.awaitTermination(5, TimeUnit.SECONDS);
                workerServiceThread.awaitTermination(5, TimeUnit.SECONDS);
                subscribeThreads.clear();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("De init done");
        }
    }
    public ExecutorService getWorkerServiceThread() {
        return workerServiceThread;
    }

}
