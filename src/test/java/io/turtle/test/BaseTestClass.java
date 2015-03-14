package io.turtle.test;

import io.turtle.env.TurtleEnvironment;
import org.junit.Ignore;

import java.util.concurrent.TimeUnit;

/**
 * Created by gabriele on 10/03/2015.
 */

@Ignore
public class BaseTestClass {

    public TurtleEnvironment getTurtleEnvironment() {
        TurtleEnvironment turtleEnvironment = new TurtleEnvironment();
        turtleEnvironment.init();
        return turtleEnvironment;
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
