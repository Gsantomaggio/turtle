package io.turtle.core.tag;

import java.util.ArrayList;

/**
 * Created by gabriele on 13/03/15.
 */
public interface TagIndex<T>{
    ArrayList<T> getSubscriberIdsByTag(String tag);
    void addTagToIndex(String tag, String subscribeId);
    void removeTagToIndex(String tag, String subscribeId);
    int getCount();
    void clear();

}
