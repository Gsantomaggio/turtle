package io.turtle.test;

import io.turtle.env.local.LocalTurtleEnvironment;
import org.junit.Ignore;

import java.util.concurrent.TimeUnit;

/**
 * Created by gabriele on 10/03/2015.
 */

@Ignore
public class BaseTestClass {

    public LocalTurtleEnvironment getTurtleEnvironment() {
        LocalTurtleEnvironment localTurtleEnvironment = new LocalTurtleEnvironment();
        localTurtleEnvironment.open();
        return localTurtleEnvironment;
    }

    public void testWait() {
       testWait(500);
    }

    public void testWait(int millisecond) {
        try {
            TimeUnit.MILLISECONDS.sleep(millisecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
