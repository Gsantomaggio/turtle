package io.turtle.example;

import com.codahale.metrics.ConsoleReporter;
import io.turtle.core.handlers.MessagesHandler;
import io.turtle.env.TurtleEnvironment;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Created by gabriele on 09/03/2015.
 */
public class SimplePublishSubscribe {

    private static final Logger log = Logger.getLogger(SimplePublishSubscribe.class.getName());

    public static void main(String[] args) throws InterruptedException {
        log.info("SimplePublishSubscribe");
        TurtleEnvironment env = new TurtleEnvironment();
        env.init();

        ConsoleReporter reporter = ConsoleReporter.forRegistry(env.getMetrics())
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(3, TimeUnit.SECONDS);


        final int message_to_sent = 1_000_000;
        log.info("Init done, press key to start test");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        List<String> listSubscriber = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        AtomicInteger messageRecvSub1 = new AtomicInteger();
        String subid = env.subscribe(new MessagesHandler() {
            @Override
            public void handlerMessage(Map header, byte[] body, String firstMatchTag) {
                if (messageRecvSub1.addAndGet(1) == message_to_sent) {
                    long endTime = System.currentTimeMillis();
                    long duration = (endTime - startTime);
                    log.info("1 - **** Duration:" + duration);
                }
            }
        }, "#pizza", "#pasta", "wine");
        listSubscriber.add(subid);


        int tag_count = 1_500_000;
        String[] p = new String[tag_count];
        for (int i = 0; i < tag_count; i++) {
            p[i] = UUID.randomUUID().toString();
        }

        AtomicInteger messageRecvSub2 = new AtomicInteger();
        String subid2 = env.subscribe(new MessagesHandler() {
            @Override
            public void handlerMessage(Map header, byte[] body, String firstMatchTag) {
                if (messageRecvSub2.addAndGet(1) == message_to_sent) {
                    long endTime = System.currentTimeMillis();
                    long duration = (endTime - startTime);
                    log.info("2 - **** Duration messageRecvSub2 :" + duration);
                }
            }
        }, p);
        listSubscriber.add(subid2);


        AtomicInteger messageRecvSub3 = new AtomicInteger();
        String subid3 = env.subscribe(new MessagesHandler() {
            @Override
            public void handlerMessage(Map header, byte[] body, String firsttMatchTag) {
             /*   try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                if (messageRecvSub3.addAndGet(1) == message_to_sent) {
                    long endTime = System.currentTimeMillis();
                    long duration = (endTime - startTime);
                    log.info("3 - **** Duration messageRecvSub3 :" + duration);
                }
            }
        }, "winef", "#spaghetti");
        listSubscriber.add(subid3);


        for (int i = 0; i < message_to_sent; i++) {
            env.publish(new String("today spaghetti and wine !!").getBytes(), "#pasta", "#wine", "#spaghetti");
            //TimeUnit.MILLISECONDS.sleep(1);
        }

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        log.info(" **** Duration publish :" + duration);
        log.info("press key to de-init");
        scanner.nextLine();
        log.info("Total messages 1:" + messageRecvSub1.get());
        log.info("Total messages 2:" + messageRecvSub2.get());
        log.info("Total messages 3:" + messageRecvSub3.get());
        listSubscriber.forEach(env::unSubscribe);
        env.deInit();
        log.info("press key to stop");
        scanner.nextLine();


    }

}
