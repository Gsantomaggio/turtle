package io.turtle.core.handlers;

import io.turtle.pubsub.Message;


/**
 * Created by gabriele on 08/03/15.
 */
public interface MessagesHandler<T> {
    void handlerMessage(Message message);
}
