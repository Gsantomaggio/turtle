package io.turtle.pubsub;

import io.turtle.core.handlers.MessagesHandler;
import io.turtle.core.tag.Tags;
import io.turtle.core.tag.impl.LocalTags;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by gabriele on 09/03/2015.
 */
public abstract class Subscriber {
    public Tags tags = new LocalTags();
    public String subscriberID = UUID.randomUUID().toString();
    public List<MessagesHandler> messageHandlers = new LinkedList<>();
}
