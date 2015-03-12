package io.turtle.example;

import io.turtle.core.handlers.MessagesHandler;
import io.turtle.env.TurtleEnvironment;
import io.turtle.pubsub.Message;
import io.turtle.pubsub.impl.StringMessage;

import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Created by gabriele on 12/03/15.
 */
public class MultiThreadingPublishSubscribe {


    private static final Logger log = Logger.getLogger(MultiThreadingPublishSubscribe.class.getName());

    public static void main(String[] args) throws InterruptedException {
        log.info("MultiThreadingPublishSubscribe");
        final TurtleEnvironment env = new TurtleEnvironment();
        env.init();
        final int message_to_sent = 1_000_000;
        log.info("Init done, press key to start test");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        AtomicInteger atomicInteger = new AtomicInteger();
        String sid = env.subscribe(new MessagesHandler<Message>() {
            @Override
            public void handlerMessage(Message message) {
               if(atomicInteger.addAndGet(1) == message_to_sent){
                   log.info("got " + atomicInteger.get());
               }
            }
        },"#hello");

        AtomicInteger counter = new AtomicInteger();
        AtomicInteger counter1 = new AtomicInteger();
        ExecutorService pollPublish = Executors.newFixedThreadPool(100);
        for (int i = 0; i <message_to_sent ; i++) {
            pollPublish.submit(()->{
                try {
                    counter.addAndGet(1);
                    env.publish(new StringMessage(),"#hello");
                    counter1.addAndGet(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        }

        log.info("press key to stop test");
        scanner = new Scanner(System.in);
        scanner.nextLine();
        pollPublish.shutdown();
        log.info("got " + atomicInteger.get() + " - " + counter.get() + " -" + counter1.get());
        env.deInit();




    }
}
