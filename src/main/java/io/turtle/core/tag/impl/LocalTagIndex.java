package io.turtle.core.tag.impl;

import io.turtle.core.tag.TagIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gabriele on 14/03/15.
 */
public class LocalTagIndex implements TagIndex {

    private Map<String, ArrayList<String>> tagIndex = new HashMap<>();
    @Override
    public ArrayList<String> getSubscriberIdsByTag(String tag) {
        return tagIndex.get(tag);
    }

    @Override
    public void addTagToIndex(String tag, String subscribeId) {
        ArrayList<String> subMapped = null;
        if (tagIndex.get(tag) == null) {
            subMapped = new ArrayList();
            tagIndex.put(tag, subMapped);
        } else subMapped = tagIndex.get(tag);

        if (subMapped.stream().filter(x -> (x.equalsIgnoreCase(subscribeId))).count() == 0) {
            subMapped.add(subscribeId);
        }
    }

    @Override
    public void removeTagToIndex(String tag, String subscriberId) {
            ArrayList<String> subMapped = tagIndex.get(tag);
            subMapped.remove(subscriberId);
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

}
