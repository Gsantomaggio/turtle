package io.turtle.pubsub.impl.nio;

import io.turtle.core.handlers.MessagesHandler;
import io.turtle.core.tag.Tags;
import io.turtle.core.tag.impl.LocalTags;
import io.turtle.pubsub.Subscriber;

import java.nio.channels.SelectionKey;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by gabriele on 07/04/15.
 */
public class NioSubscriber implements Subscriber<SelectionKey> {

    SelectionKey selectionKey;
    @Override
    public SelectionKey getSubscribeId() {
        return selectionKey;
    }

    @Override
    public void setSubscribeId(SelectionKey idType) {
        this.selectionKey = idType;
    }

    Tags tags = new LocalTags();
    @Override
    public Tags getTags() {
        return tags;
    }

    List<MessagesHandler> messagesHandlerList = new LinkedList<>();
    @Override
    public List<MessagesHandler> getMessageHandlers() {
        return messagesHandlerList;
    }
}
