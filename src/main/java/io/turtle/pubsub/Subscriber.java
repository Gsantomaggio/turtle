package io.turtle.pubsub;

import io.turtle.core.handlers.MessagesHandler;
import io.turtle.core.tag.Tags;

import java.util.List;

/**
 * Created by gabriele on 09/03/2015.
 */
public interface Subscriber<T> {
    T getSubscribeId();
    void setSubscribeId(T idType);

    Tags getTags();
    List<MessagesHandler> getMessageHandlers();



}

