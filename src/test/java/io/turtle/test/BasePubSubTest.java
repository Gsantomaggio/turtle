package io.turtle.test;

import io.turtle.core.handlers.MessagesHandler;
import io.turtle.env.TurtleEnvironment;
import io.turtle.pubsub.Message;
import io.turtle.pubsub.impl.StringMessage;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class BasePubSubTest extends BaseTestClass {


    @Test
    public void testPublish() throws Exception {
        TurtleEnvironment turtleEnvironment = new TurtleEnvironment();
        turtleEnvironment.init();
        final List<String> list = new ArrayList<>();
        final String value_message = "mytestmessage";
        StringMessage testMessage = new StringMessage();
        String subScriberId = turtleEnvironment.subscribe(new MessagesHandler<Message>() {
            @Override
            public void handlerMessage(Message message) {
                if (message instanceof StringMessage)
                    list.add(((StringMessage) message).getValue());
            }
        }, "test");

        testMessage.setValue(value_message);
        turtleEnvironment.publish(testMessage, "test");
        testWait();
        turtleEnvironment.unSubscribe(subScriberId);
        turtleEnvironment.deInit();
        assertTrue(list.size() == 1);
        assertTrue(list.get(0).equalsIgnoreCase(value_message));


    }


}