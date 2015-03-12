package io.turtle.core.routing;

import io.turtle.core.tag.Tags;
import io.turtle.pubsub.Message;

/**
 * Created by gabriele on 08/03/15.
 */
public class RoutingMessage extends Tags {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public RoutingMessage(Message message, String[] tags) {
        this.message = message;
        for (String item : tags) {
            this.addTag(item);
        }


    }


}
