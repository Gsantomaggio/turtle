package io.turtle.env.local;

import io.turtle.core.handlers.MessagesHandler;
import io.turtle.core.routing.RoutingMessage;
import io.turtle.core.tag.impl.LocalTagIndex;
import io.turtle.env.IPublisher;
import io.turtle.env.TurtleEnvironment;
import io.turtle.pubsub.impl.local.LocalSubscriber;

import java.io.IOException;
import java.util.Map;

/**
 * Created by gabriele on 09/03/2015.
 */
public class LocalTurtleEnvironment extends TurtleEnvironment<String>  {


    @Override
    public synchronized void open() {
        tagIndex = new LocalTagIndex();
        super.open();
    }



    public String subscribe(MessagesHandler messageHandler, String... tags) throws IOException, InterruptedException {
        LocalSubscriber localSubscriber = new LocalSubscriber();
        localSubscriber.getMessageHandlers().add(messageHandler);
        return super.subscribe(localSubscriber, tags);
    }



}
