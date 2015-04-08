package io.turtle.test;

import io.turtle.env.local.LocalTurtleEnvironment;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by gabriele on 22/03/15.
 */
public class SubscribeAndUnSubscribeTest extends  BaseTestClass  {


    @Test
    public void multiThreadSubscribeAndUnsubscribe() throws InterruptedException {

        final LocalTurtleEnvironment env = getTurtleEnvironment();
        final int subAndUnsub = 50;

        ExecutorService pollsubAndUnsub = Executors.newFixedThreadPool(subAndUnsub);

        for (int i = 0; i < subAndUnsub; i++) {
            pollsubAndUnsub.execute(()->{
                try {
                    env.subscribe((header, body, firstMatchTag,sourceSubscriber) -> {

                    }, "");
                } catch (IOException e) {
                    fail("fail subscribe, error: "+ e.getMessage());
                } catch (InterruptedException e) {
                    fail("fail subscribe, error: " + e.getMessage());
                }

            });
        }
        pollsubAndUnsub.shutdown();
        pollsubAndUnsub.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue("Subscribe count error before close ",env.getSubscribersCount() ==subAndUnsub );
        try {
            env.close();
        } catch (IOException e) {
            fail("Error during environment ");
        }

        assertTrue("Subscribe count error",env.getSubscribersCount() ==0 );



    }
}
