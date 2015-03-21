package io.turtle.test;

import io.turtle.core.handlers.MessagesHandler;
import io.turtle.env.TurtleEnvironment;

import org.junit.Test;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by gabriele on 10/03/15.
 */
public class InternalCountTest extends BaseTestClass {

    @Test
    public void tesInternalCount() throws Exception {
        TurtleEnvironment turtleEnvironment = new TurtleEnvironment();
        turtleEnvironment.init();
        int subCount = turtleEnvironment.getSubscribersCount();
        String subid1 = turtleEnvironment.subscribe(new MessagesHandler() {
            @Override
            public void handlerMessage(Map header, byte[] body, String firstMatchTag) {

            }
        }, "tag1", "tag2", "tag3", "tag4", "tag5", "tag6");

         subCount = turtleEnvironment.getSubscribersCount();
        String subid2 = turtleEnvironment.subscribe(new MessagesHandler() {
            @Override
            public void handlerMessage(Map header, byte[] body, String firstMatchTag) {

            }
        }, "tag6", "tag7", "tag8", "tag9", "tag10");


        assertTrue(turtleEnvironment.getTagIndexCount() == 10);
         subCount = turtleEnvironment.getSubscribersCount();
        assertTrue(subCount == 2);

        turtleEnvironment.unSubscribe(subid1);
        turtleEnvironment.unSubscribe(subid2);
        assertTrue(turtleEnvironment.getTagIndexCount() == 0);
        assertTrue(turtleEnvironment.getSubscribersCount() == 0);

        turtleEnvironment.deInit();
    }
}
