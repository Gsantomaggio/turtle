package io.turtle.nio.server.protocol;

import io.turtle.core.routing.RoutingMessage;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by gabriele on 23/03/15.
 */
public class ProtocolUtils {




    private static int getStringLen(String value) {
        return Integer.BYTES + value.length();
    }

    private static void writeByteString(ByteBuffer byteBuffer, String value) {
        byteBuffer.putInt(value.length());
        byteBuffer.put(value.getBytes());

    }


    public static ByteBuffer writePublishMessage(Map<String, String> header, byte[] body,AtomicInteger bytes,AtomicInteger size, String... tags) {
        return  writeMessage(header,body,bytes,size,Protocol.HEADER_COMMAND_PUBLISH,tags);
    }

    public static ByteBuffer writeSubscribe(AtomicInteger bytes,AtomicInteger size, String... tags) {
        return  writeMessage(null,null,bytes,size,Protocol.HEADER_COMMAND_SUBSCRIBE,tags);
    }

    public static ByteBuffer writeMessage(Map<String, String> header, byte[] body,AtomicInteger bytes,AtomicInteger size,int command, String... tags) {
        //message len
        int message_len=Integer.BYTES;
        // protocol version len
        message_len += Integer.BYTES;
        // command type len
        message_len += Integer.BYTES;

        message_len += Integer.BYTES;
        if (header != null) {
            // header len

            for (String key : header.keySet()) {
                message_len += getStringLen(key);
                message_len += getStringLen(header.get(key));
            }
        }
        // body len
        if (body!=null) {
            message_len += Integer.BYTES + body.length;
        } else message_len += Integer.BYTES;

        // tags len
        message_len += Integer.BYTES;
        for (String tag : tags) {
            message_len += getStringLen(tag);
        }


        // allocation buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(message_len);
        if (size!=null) size.set(message_len);
        //write message len
        byteBuffer.putInt(message_len);
        if (bytes!=null) bytes.addAndGet(message_len);

        // write protocol
        byteBuffer.putInt(Protocol.CURRENT_PROTOCOL_VERSION);

        // write command type
        byteBuffer.putInt(command);

        // write header
        if (header != null) {
            byteBuffer.putInt(header.size());
            for (String key : header.keySet()) {
                writeByteString(byteBuffer, key);
                writeByteString(byteBuffer, header.get(key));
            }
        } else byteBuffer.putInt(0);


        //write body
        if (body!=null) {
            byteBuffer.putInt(body.length);
            byteBuffer.put(body);
        } else byteBuffer.putInt(0);

        //write body
        byteBuffer.putInt(tags.length);
        for (String tag : tags) {
            writeByteString(byteBuffer, tag);
        }

        return byteBuffer;

    }



    private static String readString(ByteBuffer byteBuffer) {
        int len = byteBuffer.getInt();
        byte[] bodyString = new byte[len];
        byteBuffer.get(bodyString);
        return new String(bodyString);

    }

    public static RoutingMessage getRoutingMessage(ByteBuffer buffer) {
        int headerSize = buffer.getInt();

        // header
        Map<String, String> header = null;
        if (headerSize > 0) {
            header = new HashMap<>();
            for (int i = 0; i < headerSize; i++) {
                String key = readString(buffer);
                String value = readString(buffer);
                header.put(key, value);
            }
        }
        //body
        int bodyLen = buffer.getInt();
        byte[] bodyMessage = new byte[bodyLen];
        buffer.get(bodyMessage);

        //tags
        int tagLen = buffer.getInt();
        String[] tags = new String[tagLen];
        for (int i = 0; i < tagLen; i++) {
            tags[i] = readString(buffer);
        }

        return new RoutingMessage(header, bodyMessage, tags);
    }





    public static int getBodySize(ProtocolHeader protocolHeader) {
        return protocolHeader.getMessageLen() - Protocol.HEADER_BYTES_LEN;
    }

    public static ProtocolHeader checkHeaderIntegrity(ByteBuffer headerBuffer) throws Exception {
        ProtocolHeader protocolHeader = new ProtocolHeader();
        protocolHeader.setMessageLen(headerBuffer.getInt());
        protocolHeader.setProtocolVersion(headerBuffer.getInt());
        if (protocolHeader.getProtocolVersion() != Protocol.CURRENT_PROTOCOL_VERSION){
            throw new Exception("Protocol version mismatch, server:".concat(""+Protocol.CURRENT_PROTOCOL_VERSION).concat(" Client:").concat("" + protocolHeader.getProtocolVersion()) );
        }
        protocolHeader.setCommandType(headerBuffer.getInt());

        return protocolHeader;
    }


}
