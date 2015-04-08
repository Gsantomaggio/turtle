package io.turtle.core.tag.impl;

import io.turtle.core.tag.TagIndex;
import io.turtle.core.tag.Tags;

import java.util.ArrayList;

/**
 * Created by gabriele on 14/03/15.
 */
public class LocalTags implements Tags {
    private ArrayList<String> tags = new ArrayList<>();
    @Override
    public void addTag(String tag) {
        tags.add(tag);
    }

    @Override
    public ArrayList<String> getTags() {
        return tags;
    }

    @Override
    public long containsTag(String tag) {
        return tags.stream().filter(x -> (x.equalsIgnoreCase(tag))).count();
    }
}
