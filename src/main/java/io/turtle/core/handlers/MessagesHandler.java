package io.turtle.core.handlers;


import java.util.Map;
import java.util.concurrent.Future;


/**
 * Created by gabriele on 08/03/15.
 * Generic interface to handle messages.
 *
 */
public interface MessagesHandler<T> {
    void handlerMessage(Map<String,String> header, byte[] body,String firstMatchTag);
}
