package io.turtle.test;

import io.turtle.core.handlers.MessagesHandler;
import io.turtle.env.TurtleEnvironment;
import org.junit.Test;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by gabriele on 12/03/15.
 */
public class MultiThreadingPublishTest extends BaseTestClass {

    /**
     * Test to check the synchronization call back.
     * the messages for each call back
     * @throws InterruptedException
     */
    @Test
    public void multiThreadPublish() throws InterruptedException {
        final TurtleEnvironment env = getTurtleEnvironment();
        final int message_to_sent = 50;

        AtomicInteger nosleepMessageCount = new AtomicInteger();
        String sid =  env.subscribe(new MessagesHandler() {
            @Override
            public void handlerMessage(Map header, byte[] body, String firstMatchTag) {
                System.out.println("Received #hello not sleep message");

                nosleepMessageCount.addAndGet(1);

            }
        },"#hello","#tag1");


        env.subscribe(new MessagesHandler() {
            @Override
            public void handlerMessage(Map header, byte[] body, String firstMatchTag) {
                System.out.println("Received #tag1 not sleep message");
                nosleepMessageCount.addAndGet(1);

            }
        },"#tag1");


        AtomicInteger sleepCountMessages = new AtomicInteger();
        String si2 = env.subscribe(new MessagesHandler() {
            @Override
            public void handlerMessage(Map header, byte[] body, String firstMatchTag) {
                System.out.println("Received #tag1 sleep message");
                testWait(50);
                sleepCountMessages.addAndGet(1);
            }
        },"#tag1");
        ExecutorService pollPublish = Executors.newFixedThreadPool(100);
        for (int i = 0; i <message_to_sent ; i++) {
            pollPublish.submit(()->{
                try {
                   env.publish(new String("hello").getBytes(), "#tag1","#hello");

                } catch (InterruptedException e) {
                    fail("error during publish" + e);
                }
            });
        }

        testWait(5000);
        pollPublish.shutdownNow();
        pollPublish.awaitTermination(5, TimeUnit.SECONDS);
        assertTrue("Different sleep message count", sleepCountMessages.get() == message_to_sent);
        assertTrue("Different no sleep message count",nosleepMessageCount.get()==message_to_sent*2);
        env.deInit();

    }
}
