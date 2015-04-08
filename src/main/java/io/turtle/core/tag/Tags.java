package io.turtle.core.tag;

import java.util.ArrayList;

/**
 * Created by gabriele on 09/03/2015.
 */
public interface Tags {
     void addTag(String tag);
     ArrayList<String> getTags();
     long containsTag(String tag);

}
