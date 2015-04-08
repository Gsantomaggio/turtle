package io.turtle.pubsub.impl.local;

import io.turtle.core.handlers.MessagesHandler;
import io.turtle.core.tag.Tags;
import io.turtle.core.tag.impl.LocalTags;
import io.turtle.pubsub.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by gabriele on 09/03/2015.
 */
public class LocalSubscriber implements Subscriber<String> {

    private  String subscriberID = UUID.randomUUID().toString();

    @Override
    public String getSubscribeId() {
        return subscriberID;
    }

    @Override
    public void setSubscribeId(String idType) {
         this.subscriberID = idType;
    }

    Tags tags = new LocalTags();
    @Override
    public Tags getTags() {
        return tags;
    }

    List<MessagesHandler> messagesHandlerList = new ArrayList<>();

    @Override
    public List<MessagesHandler> getMessageHandlers() {

        return messagesHandlerList;
    }
}
