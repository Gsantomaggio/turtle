package io.turtle.core.routing;

import io.turtle.core.tag.Tags;
import io.turtle.core.tag.impl.LocalTags;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gabriele on 08/03/15.
 */
public class RoutingMessage extends LocalTags {

    private byte[] body;
    private Map<String,String> header;
    public Map<String,String> getHeader(){
        return  header;
    }
    public byte[] getBody() {
        return body;
    }

    public RoutingMessage(byte[] body, Map<String,String> header,String[] tags) {
        this.body = body;
        this.header = header;
        for (String item : tags) {
            this.addTag(item);
        }
    }


}
