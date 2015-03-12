package io.turtle.core.tag;

import java.util.ArrayList;

/**
 * Created by gabriele on 09/03/2015.
 */
public class Tags {

    private ArrayList<String> tags = new ArrayList<>();

    public void addTag(String tag) {
        tags.add(tag);
    }

    public ArrayList<String> getTags() {
        return tags;
    }


    public long containsTag(String tag) {
        return tags.stream().filter(x -> (x.equalsIgnoreCase(tag))).count();
    }


}
