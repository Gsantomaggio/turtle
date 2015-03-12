package io.turtle.test;

import org.junit.Ignore;

import java.util.concurrent.TimeUnit;

/**
 * Created by gabriele on 10/03/2015.
 */

@Ignore
public class BaseTestClass {

    public void testWait() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
            System.out.print("aaa");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
