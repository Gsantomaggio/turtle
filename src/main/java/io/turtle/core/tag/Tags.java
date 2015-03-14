package io.turtle.core.tag;

import java.util.ArrayList;

/**
 * Created by gabriele on 09/03/2015.
 */
public interface Tags {
    public void addTag(String tag);
    public ArrayList<String> getTags();
    public long containsTag(String tag);

}
