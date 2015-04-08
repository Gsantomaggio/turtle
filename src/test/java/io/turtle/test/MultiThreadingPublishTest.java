package io.turtle.test;

import io.turtle.env.local.LocalTurtleEnvironment;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by gabriele on 12/03/15.
 */
public class MultiThreadingPublishTest extends BaseTestClass {

    /**
     * Test to check the synchronization call back.
     * the messages for each call back
     *
     * @throws InterruptedException
     */
    @Test
    public void multiThreadPublish() throws InterruptedException {
        final LocalTurtleEnvironment env = getTurtleEnvironment();
        final int message_to_sent = 50;

        AtomicInteger nosleepMessageCount = new AtomicInteger();
        AtomicInteger sleepCountMessages = new AtomicInteger();
        try {
            String sid = env.subscribe((header, body, firstMatchTag,sourceSubscriber) -> {
                System.out.println("Received #hello not sleep message");

                nosleepMessageCount.addAndGet(1);

            }, "#hello", "#tag1");


            env.subscribe((header, body, firstMatchTag,sourceSubscriber) -> {
                System.out.println("Received #tag1 not sleep message");
                nosleepMessageCount.addAndGet(1);

            }, "#tag1");



            String si2 = env.subscribe((header, body, firstMatchTag,sourceSubscriber) -> {
                System.out.println("Received #tag1 sleep message");
                testWait(50);
                sleepCountMessages.addAndGet(1);
            }, "#tag1");
        } catch (IOException e) {
            fail("subscribe error:" + e.getMessage());
        }
        ExecutorService pollPublish = Executors.newFixedThreadPool(100);
        for (int i = 0; i < message_to_sent; i++) {
            pollPublish.submit(() -> {
                try {
                    try {
                        env.publish(new String("hello").getBytes(), "#tag1", "#hello");
                    } catch (IOException e) {
                        fail("error during publish IO" + e);
                    }

                } catch (InterruptedException e) {
                    fail("error during publish" + e);
                }
            });
        }

        testWait(5000);
        pollPublish.shutdownNow();
        pollPublish.awaitTermination(5, TimeUnit.SECONDS);
        assertTrue("Different sleep message count", sleepCountMessages.get() == message_to_sent);
        assertTrue("Different no sleep message count", nosleepMessageCount.get() == message_to_sent * 2);
        try {
            env.close();
        } catch (IOException e) {
            fail("error close environment" + e);
        }

    }
}
