package io.turtle.test;

import io.turtle.core.handlers.MessagesHandler;
import io.turtle.env.TurtleEnvironment;
import io.turtle.pubsub.Message;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by gabriele on 10/03/15.
 */
public class InternalCountTest extends BaseTestClass {


    @Test
    public void testName() throws Exception {
        TurtleEnvironment turtleEnvironment = new TurtleEnvironment();
        turtleEnvironment.init();
        String subid1 = turtleEnvironment.subscribe(new MessagesHandler<Message>() {
            @Override
            public void handlerMessage(Message message) {

            }
        }, "tag1", "tag2", "tag3", "tag4", "tag5", "tag6");

        String subid2 = turtleEnvironment.subscribe(new MessagesHandler<Message>() {
            @Override
            public void handlerMessage(Message message) {

            }
        }, "tag6", "tag7", "tag8", "tag9", "tag10");


        assertTrue(turtleEnvironment.getResourcesCounter().getTagIndexCount() == 10);
        assertTrue(turtleEnvironment.getResourcesCounter().getSubscriberCount() == 2);

        turtleEnvironment.unSubscribe(subid1);
        turtleEnvironment.unSubscribe(subid2);
        assertTrue(turtleEnvironment.getResourcesCounter().getTagIndexCount() == 0);
        assertTrue(turtleEnvironment.getResourcesCounter().getSubscriberCount() == 0);


        turtleEnvironment.deInit();

    }
}
