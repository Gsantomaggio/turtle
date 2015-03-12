package io.turtle.pubsub.impl;

import io.turtle.pubsub.Message;

/**
 * Created by gabriele on 08/03/15.
 */
public class StringMessage<T> implements Message {

    public StringMessage() {

    }

    private String value = "";


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
