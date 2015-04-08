package io.turtle.core.services;

import io.turtle.configuration.Configuration;
import io.turtle.core.handlers.MessagesHandler;
import io.turtle.core.services.utils.TurtleThreadFactory;
import io.turtle.core.tag.TagIndex;
import io.turtle.core.tag.impl.LocalTagIndex;
import io.turtle.pubsub.Subscriber;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Created by gabriele on 08/03/15.
 * Resources is the  core class, contains threads
 */
public class Resources{



    private static Resources instance = null;
    protected Resources() {
        // Exists only to defeat instantiation.
    }
    public static Resources getInstance() {
        if(instance == null) {
            instance = new Resources();
        }
        return instance;
    }


    private static final Logger log = Logger.getLogger(Resources.class.getName());
    private ExecutorService internalServiceThread = null;
    private ExecutorService workerServiceThread = null;
    private ConcurrentHashMap<String, Subscriber> subscribers = new ConcurrentHashMap<>();



    private Configuration currentConfiguration;
    private TagIndex tagIndex = new LocalTagIndex();
    public TagIndex getTagIndex(){
        return tagIndex;
    }



    public Map<String, Subscriber> getSubscribers() {
        return subscribers;
    }


    public ExecutorService getServiceThread(){
        return internalServiceThread;
    }


    public void init(Configuration configuration) {
        currentConfiguration = configuration;
        int threadCount = configuration.getDispatchThreadCount();
        log.info(" Init resources, core:" + threadCount + " - Subscribe Threads: " + configuration.getSubscribeThreadCount() + " - Publish Threads: " + configuration.getPublishThreadCount());
        internalServiceThread = Executors.newFixedThreadPool(threadCount, new TurtleThreadFactory("ServiceThread"));
        /*for (int i = 0; i < configuration.getSubscribeThreadCount(); i++) {
            SubscribeThread subscribeThread = new SubscribeThread();

            subscribeThreads.put(new Integer(i),subscribeThread);
            internalServiceThread.submit(subscribeThread);
        }

        for (int i = 0; i < configuration.getPublishThreadCount(); i++) {
            PublishThread publishThread = new PublishThread();
            publishThreads.put(new Integer(i), publishThread);
            internalServiceThread.submit(publishThread);
        }*/
        workerServiceThread = Executors.newFixedThreadPool(configuration.getWorkersThreadCount(), new TurtleThreadFactory("Worker-Thread"));
    }






    public void deInit() {
        if (internalServiceThread != null) {
            log.info("De open resources");
            internalServiceThread.shutdownNow();
            workerServiceThread.shutdownNow();
            try {
                internalServiceThread.awaitTermination(5, TimeUnit.SECONDS);
                workerServiceThread.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("De open done");
        }
    }
    public ExecutorService getWorkerServiceThread() {
        return workerServiceThread;
    }

}
