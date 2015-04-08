package io.turtle.nio.server.protocol;

/**
 * Created by gabriele on 31/03/2015.
 */
public class Protocol {

    public static final int CURRENT_PROTOCOL_VERSION = 2;
    public static final int HEADER_BYTES_LEN = 12; // see Protcol Herader


    public static final int HEADER_COMMAND_PUBLISH = 10;
    public static final int HEADER_COMMAND_SUBSCRIBE = 20;
    public static final int HEADER_COMMAND_UNSUBSCRIBE = 21;


}
