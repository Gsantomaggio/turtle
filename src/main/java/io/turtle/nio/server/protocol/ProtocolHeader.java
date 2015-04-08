package io.turtle.nio.server.protocol;

/**
 * Created by gabriele on 31/03/2015.
 */
public class ProtocolHeader {

    private int protocolVersion;
    private int messageLen;
    private int commandType;

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public int getMessageLen() {
        return messageLen;
    }

    public void setMessageLen(int messageLen) {
        this.messageLen = messageLen;
    }

    public int getCommandType() {
        return commandType;
    }

    public void setCommandType(int commandType) {
        this.commandType = commandType;
    }
}
