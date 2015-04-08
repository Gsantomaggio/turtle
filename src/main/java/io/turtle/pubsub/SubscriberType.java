package io.turtle.pubsub;

import io.turtle.core.handlers.MessagesHandler;
import io.turtle.core.tag.Tags;
import io.turtle.core.tag.impl.LocalTags;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gabriele on 06/04/15.
 */
public interface SubscriberType<T> {

    T getSubscribeId();
     void setSubscribeId(T idType);

    Tags getTags();
    List<MessagesHandler> getMessageHandlers();


}
