package io.turtle.env;

import io.turtle.core.routing.RoutingMessage;

import java.io.IOException;
import java.util.Map;

/**
 * Created by gabriele on 06/04/15.
 */
public interface IPublisher {


    public abstract void publish(Map<String, String> header, byte[] body, String... tags) throws InterruptedException, IOException;

    public abstract void publish(RoutingMessage routingMessage) throws InterruptedException;

    public abstract void publish(byte[] body, String... tags) throws InterruptedException, IOException;

}
