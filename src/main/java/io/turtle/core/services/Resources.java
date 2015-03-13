package io.turtle.core.services;

import io.turtle.configuration.Configuration;
import io.turtle.pubsub.Subscriber;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Created by gabriele on 08/03/15.
 */
public class Resources {


    private class WorkerThreadFactory implements ThreadFactory {
        private int counter = 0;

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "WorkerThread-" + counter++);

            return t;
        }
    }

    private class ServiceThreadFactory implements ThreadFactory {
        private int counter = 0;

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "ServiceThread-" + counter++);

            return t;
        }
    }


    private static final Logger log = Logger.getLogger(Resources.class.getName());
    private ExecutorService internalServiceThread = null;
    private ExecutorService workerServiceThread = null;
    private ConcurrentHashMap<String, Subscriber> subscribers = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Integer,SubscribeThread> subscribeThreads = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer,PublishThread> publishThreads = new ConcurrentHashMap<>();
    public AtomicInteger totalMessagesPublished = new AtomicInteger();
    public AtomicInteger totalMessagesDeliveredByWorker = new AtomicInteger();
    public AtomicInteger totalMessagesDelivered = new AtomicInteger();

    private Map<String, ArrayList<String>> tagIndex = new HashMap<>();

    public Map getTagIndex() {
        return tagIndex;
    }


    private Configuration currentConfiguration;


    public Map<Integer,PublishThread> getPublishThreads() {
        return publishThreads;
    }

    public Map<Integer,SubscribeThread> getSubscribeThreads() {
        return subscribeThreads;
    }

    public Map<String, Subscriber> getSubscribers() {
        return subscribers;

    }

    public void incMessagesPublished() {
        totalMessagesPublished.incrementAndGet();
    }

    public void incMessagesDelivered() {
        totalMessagesDelivered.incrementAndGet();
    }

    public Configuration getCurrentConfiguration() {
        return currentConfiguration;
    }


    public void init(Configuration configuration) {
        currentConfiguration = configuration;
        int threadCount = configuration.getDispatchThreadCount();
        log.info(" Init resources, core:" + threadCount + " - Subscribe Threads: " + configuration.getSubscribeThreadCount() + " - Publish Threads: " + configuration.getPublishThreadCount());
        internalServiceThread = Executors.newFixedThreadPool(threadCount, new ServiceThreadFactory());
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
        workerServiceThread = Executors.newFixedThreadPool(configuration.getWorkersThreadCount(), new WorkerThreadFactory());
    }


    public void addServiceThread(Thread thread) {
        internalServiceThread.submit(thread);
    }


    public ArrayList<String> getSubscriberIdsbyTag(String tag) {
        return tagIndex.get(tag);
    }

    private void addTagToIndex(String tag, String subscribeId) {
        ArrayList<String> subMapped = null;
        if (tagIndex.get(tag) == null) {
            subMapped = new ArrayList();
            tagIndex.put(tag, subMapped);
        } else subMapped = tagIndex.get(tag);

        if (subMapped.stream().filter(x -> (x.equalsIgnoreCase(subscribeId))).count() == 0) {
            subMapped.add(subscribeId);
        }
    }

    public void registerSubscriber(String key, Subscriber subscriber) {
        subscribers.put(key, subscriber);
        subscriber.tags.getTags().forEach(x -> addTagToIndex(x, subscriber.subscriberID));
    }


    public void unRegisterSubscriber(String key) {
        Subscriber subscriber = subscribers.get(key);
        subscriber.tags.getTags().forEach(x -> {
            ArrayList<String> subMapped = tagIndex.get(x);
            subMapped.remove(key);
            if (subMapped.isEmpty()) tagIndex.remove(x);
        });

        subscribers.remove(key);

    }


    public void deInit() {
        if (internalServiceThread != null) {
            log.info(" De-Init resources");
            internalServiceThread.shutdownNow();
            workerServiceThread.shutdownNow();
            try {
                internalServiceThread.awaitTermination(5, TimeUnit.SECONDS);
                workerServiceThread.awaitTermination(5, TimeUnit.SECONDS);
                subscribeThreads.clear();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public ExecutorService getWorkerServiceThread() {
        return workerServiceThread;
    }


}
