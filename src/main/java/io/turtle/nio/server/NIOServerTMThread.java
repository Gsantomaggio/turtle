package io.turtle.nio.server;

import io.turtle.metrics.impl.DropwizardTCounter;
import io.turtle.nio.server.protocol.Protocol;
import io.turtle.nio.server.protocol.ProtocolHeader;
import io.turtle.nio.server.protocol.ProtocolUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by gabriele on 22/03/15.
 */
public abstract class NIOServerTMThread extends Thread {

    private static final Logger log = Logger.getLogger(NIOServerTMThread.class.getName());


    int DEFAULT_PORT = 6667;
    ServerSocketChannel serverSocketChannel = null;
    Selector selector = null;

    ByteBuffer buff1 = ByteBuffer.allocate(Protocol.HEADER_BYTES_LEN);
    ByteBuffer buff2 = null;
    private ByteBuffer[] buffer = new ByteBuffer[]{buff1, buff2};

    private Map<SocketChannel, String> keepDataTrack = new ConcurrentHashMap<>();

    private DropwizardTCounter byteReceived = new DropwizardTCounter();
    private DropwizardTCounter enterLoopNIO = new DropwizardTCounter();
    //private DropwizardTCounter messagesDecoded = new DropwizardTCounter();

    private void startServer() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        byteReceived.register("NIOServerTMThread.BytesRcv");
        enterLoopNIO.register("DEBUG.EnterLoop");

        selector = Selector.open();
        //configure non-blocking mode
        serverSocketChannel.configureBlocking(false);
        //set some options
        serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 256 * 1024);
        serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);


        //bind the server socket channel to port
        serverSocketChannel.bind(new InetSocketAddress(DEFAULT_PORT));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);


        log.info("Waiting for connections ...");

    }

    @Override
    public void run() {

        try {
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!this.isInterrupted()) {

            try {
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Iterator keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                SelectionKey key = (SelectionKey) keys.next();

                //prevent the same key from coming up again
                keys.remove();


                if (!key.isValid()) {
                    continue;
                }

                if (key.isAcceptable()) {
                    acceptOP(key, selector);
                } else if (key.isReadable()) {
                    this.readOP(key);
                } else if (key.isWritable()) {
                    this.writeOP(key);
                }

            }
        }
    }


    private void acceptOP(SelectionKey key, Selector selector) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        SocketChannel socketChannel = null;
        try {
            socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            socketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

            keepDataTrack.put(socketChannel, "");
            socketChannel.register(selector, SelectionKey.OP_READ);
            System.out.println("Client is connected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private long readBuffer(SelectionKey key, int offset) {
        long numRead = 0;
        SocketChannel socketChannel = (SocketChannel) key.channel();
        while (buffer[offset].hasRemaining()) {
            enterLoopNIO.inc();
            try {
                numRead += socketChannel.read(buffer, offset, 1);
            } catch (IOException e) {
                log.severe("error reading buffer");
                numRead = -1;
            }
            if (numRead == -1) break;
        }
        try {
            if (numRead == -1) {
                this.keepDataTrack.remove(socketChannel);
                log.info("Connection closed by: " + socketChannel.getRemoteAddress());
                socketChannel.close();
                onRemoveClient(key);
                key.cancel();
            }
        } catch (IOException e) {
            log.severe("error reading buffer negative value");
        }

        return numRead;
    }


    private void readOP(SelectionKey key) {
        try {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            for (ByteBuffer byteBuffer : buffer) {
                if (byteBuffer != null)
                    byteBuffer.clear();
            }
            long numRead;

            numRead = readBuffer(key, 0);

            if (!socketChannel.isOpen()) return;

            if (numRead > 0) {
                ProtocolHeader protocolHeader = null;
                try {
                    byteReceived.inc(numRead);
                    buffer[0].flip();
                    protocolHeader = ProtocolUtils.checkHeaderIntegrity(buffer[0]);
                } catch (Exception e) {
                    log.severe("Check header integrity failed ! cause: " + e.getMessage());
                }


                switch (protocolHeader.getCommandType()) {
                    case Protocol.HEADER_COMMAND_PUBLISH:
                    case Protocol.HEADER_COMMAND_SUBSCRIBE: {
                        if (handleMessage(key, socketChannel, protocolHeader)) return;
                        break;
                    }

                }
            }


        } catch (Exception ex) {
            log.severe("Error read operation, cause: " + ex.getMessage());
            ex.printStackTrace();
        }


    }

    private boolean handleMessage(SelectionKey key, SocketChannel socketChannel, ProtocolHeader protocolHeader) {
        long numRead;
        numRead = 0;

        int offset = 1;
        buffer[offset] = ByteBuffer.allocate(ProtocolUtils.getBodySize(protocolHeader));
        numRead = readBuffer(key, offset);

        if (socketChannel.isOpen()) {

            byteReceived.inc(numRead);

            try {
                buffer[1].flip();

                switch (protocolHeader.getCommandType()) {
                    case Protocol.HEADER_COMMAND_PUBLISH: {
                        onPublish(buffer[1],key);
                        break;
                    }
                    case Protocol.HEADER_COMMAND_SUBSCRIBE: {
                        onRegisterSubscribe(buffer[1], key);
                        break;
                    }
                }

            } catch (Exception ex) {
                log.severe("Error on read, cause: " + ex.getMessage());
            }
            return true;
        }

        return false;
    }

    private void writeOP(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer dummyResponse = null;
        try {
            dummyResponse = ByteBuffer.wrap("ok".getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            socketChannel.write(dummyResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (dummyResponse.remaining() > 0) {
            System.err.print("Filled UP");
        }

        key.interestOps(SelectionKey.OP_READ);
    }


    public void stopServer() throws IOException {
        log.info("Stopping server");
        selector.close();
        serverSocketChannel.close();
        log.info("Server stopped");
    }


    /**
     * Override with something meaningful
     *
     * @param buf
     */
    protected abstract void onPublish(ByteBuffer buf,SelectionKey key) throws Exception;

    protected abstract void onRegisterSubscribe(ByteBuffer buf, SelectionKey key) throws Exception;

    protected abstract void onRemoveClient(SelectionKey key);


}
