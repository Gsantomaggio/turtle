package io.turtle.test;

import io.turtle.core.handlers.MessagesHandler;
import io.turtle.env.TurtleEnvironment;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class BasePubSubTest extends BaseTestClass {


    @Test
    public void testPublish() throws Exception {
        TurtleEnvironment turtleEnvironment = getTurtleEnvironment();
        final List<String> list = new ArrayList<>();
        final String value_message = "Pasta and wine!!";
        final String firstMatchTag="pasta";
        String subScriberId = turtleEnvironment.subscribe(new MessagesHandler() {
            @Override
            public void handlerMessage(Map header, byte[] body, String firstMatchTag) {

                    list.add(new String(body));
                    list.add(firstMatchTag);
            }
        }, firstMatchTag);
        turtleEnvironment.publish(value_message.getBytes(), "test3","notest4",firstMatchTag);
        testWait();
        turtleEnvironment.unSubscribe(subScriberId);
        turtleEnvironment.deInit();
        assertTrue(list.size() == 2);
        assertTrue(list.get(0).equalsIgnoreCase(value_message));
        assertTrue(list.get(1).equalsIgnoreCase(firstMatchTag));
    }


}