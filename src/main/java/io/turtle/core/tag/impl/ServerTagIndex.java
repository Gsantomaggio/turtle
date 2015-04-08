package io.turtle.core.tag.impl;

import io.turtle.core.tag.TagIndex;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gabriele on 01/04/2015.
 */
public class ServerTagIndex implements TagIndex<SelectionKey> {

    private Map<String, ArrayList<SelectionKey>> tagIndex = new HashMap<>();

    @Override
    public ArrayList<SelectionKey> getSubscriberIdsByTag(String tag) {
        return  tagIndex.get(tag);
    }

    @Override
    public void addTagToIndex(String tag, SelectionKey subscribeId) {

        ArrayList<SelectionKey> subMapped;
        if (tagIndex.get(tag) == null) {
            subMapped = new ArrayList();
            tagIndex.put(tag, subMapped);
        } else subMapped = tagIndex.get(tag);

        if (subMapped.stream().filter(x -> (x == subscribeId)).count() == 0) {
            subMapped.add(subscribeId);
        }

    }

    @Override
    public void removeTagToIndex(String tag, SelectionKey subscribeId) {
        ArrayList<SelectionKey> subMapped = tagIndex.get(tag);
        subMapped.remove(subscribeId);
        if (subMapped.isEmpty()) tagIndex.remove(tag);
    }

    @Override
    public int getCount() {
        return tagIndex.size();
    }

    @Override
    public void clear() {
        tagIndex.clear();
    }

    public void removeReferenceFromSubrscriber(SelectionKey subscribeId){






    }
}
