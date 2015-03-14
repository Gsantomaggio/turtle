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

/**
 * Created by gabriele on 12/03/15.
 */
public class MultiThreadingPublishTest extends BaseTestClass {


    @Test
    public void multiThreadPublish() throws InterruptedException {
        final TurtleEnvironment env = getTurtleEnvironment();
        final int message_to_sent = 1_000;

        AtomicInteger messagesReceived1 = new AtomicInteger();
        String sid =  env.subscribe(new MessagesHandler() {
            @Override
            public void handlerMessage(Map header, byte[] body, String firstMatchTag) {
                messagesReceived1.addAndGet(1);

            }
        },"#hello");


        AtomicInteger messagesReceived2 = new AtomicInteger();


        String si2 = env.subscribe(new MessagesHandler() {
            @Override
            public void handlerMessage(Map header, byte[] body, String firstMatchTag) {
                messagesReceived2.addAndGet(1);
            }
        },"#hello");



        ExecutorService pollPublish = Executors.newFixedThreadPool(100);
        for (int i = 0; i <message_to_sent ; i++) {
            pollPublish.submit(()->{
                try {
                    env.publish(new String("hello").getBytes(), "#hello");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        testWait(2000);
        pollPublish.shutdownNow();
        pollPublish.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(messagesReceived2.get() == message_to_sent);
        assertTrue(messagesReceived1.get()==message_to_sent);
        //log.info("got " + atomicInteger.get() + " - " + counter.get() + " -" + counter1.get());
        env.deInit();

    }
}
