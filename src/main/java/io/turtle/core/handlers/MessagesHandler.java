package io.turtle.core.handlers;


import java.util.Map;


/**
 * Created by gabriele on 08/03/15.
 * Generic interface to handle messages.
 *
 */
public interface MessagesHandler<T> {
     void handleMessage(Map<String,String> header, byte[] body,String firstMatchTag,T sourceSubscriber);
}
