package io.turtle.nio.client;

import io.turtle.metrics.TCounter;
import io.turtle.metrics.impl.DropwizardTCounter;
import io.turtle.nio.server.protocol.Protocol;
import io.turtle.nio.server.protocol.ProtocolHeader;
import io.turtle.nio.server.protocol.ProtocolUtils;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Created by gabriele on 22/03/15.
 */
public abstract class NIOClientTMThread extends Thread {
    private static final Logger log = Logger.getLogger(NIOClientTMThread.class.getName());

    int DEFAULT_PORT = 6667;
    String IP = "localhost";
    Selector selector;
    SocketChannel socketChannel;
    TCounter clientTMThreadMessagesCount = new DropwizardTCounter();
    TCounter clientTMThreadBytesRcv = new DropwizardTCounter();
    public NIOClientTMThread(){
        clientTMThreadMessagesCount.register("NIOClientTMThread.Client.MessagesCount");
        clientTMThreadBytesRcv.register("NIOClientTMThread.Client.BytesRcv");
    }
    private void connect() throws IOException {
        selector = Selector.open();
        socketChannel = SocketChannel.open();
        if ((socketChannel.isOpen()) && (selector.isOpen())) {
            socketChannel.configureBlocking(false);

            socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 0x100000);
            socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 0x100000);

            socketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new java.net.InetSocketAddress(IP, DEFAULT_PORT));
        }
    }


    private void processSelectedKeys(Set keys) throws Exception {
        Iterator itr = keys.iterator();
        while (itr.hasNext()) {
            SelectionKey key = (SelectionKey) itr.next();
            if (key.isReadable()) processRead(key);
            if (key.isWritable()) processWrite(key);
            if (key.isConnectable()) processConnect(key);
            if (key.isAcceptable()) ;
            itr.remove();
        }
    }

    private void processConnect(SelectionKey key) throws Exception {
        SocketChannel ch = (SocketChannel) key.channel();
        if (ch.finishConnect()) {
            log.info("connected to ");
            key.interestOps(key.interestOps() ^ SelectionKey.OP_CONNECT);
            key.interestOps(key.interestOps() | SelectionKey.OP_READ);
            //reconnectInterval = INITIAL_RECONNECT_INTERVAL;
            //connected.set(true);
            onConnected();
        }
    }

    private AtomicLong bytesOut = new AtomicLong(0L);
    private AtomicLong bytesIn = new AtomicLong(0L);

    private void processWrite(SelectionKey key) throws IOException {

        WritableByteChannel ch = (WritableByteChannel) key.channel();
        synchronized (writeBuf) {
            writeBuf.flip();

            int bytesOp = 0, bytesTotal = 0;
            while (writeBuf.hasRemaining() && (bytesOp = ch.write(writeBuf)) > 0)
                bytesTotal += bytesOp;

            bytesOut.addAndGet(bytesTotal);
            //   int internalbyteSent= socketChannel.write(buffer);
            bytesSent.addAndGet(bytesTotal);

            if (writeBuf.remaining() == 0) {
                key.interestOps(key.interestOps() ^ SelectionKey.OP_WRITE);
            }

            if (bytesTotal > 0) writeBuf.notify();
            else if (bytesOp == -1) {
                log.info("peer closed write channel");
                ch.close();
            }

            writeBuf.compact();
        }
    }


    ByteBuffer buff1 = ByteBuffer.allocate(Protocol.HEADER_BYTES_LEN);
    ByteBuffer buff2 = null;
    private ByteBuffer[] readBuf = new ByteBuffer[]{buff1, buff2};

    private void processRead(SelectionKey key) throws Exception {

        SocketChannel ch = (SocketChannel) key.channel();
        int bytesOp = 0, bytesTotal = 0;
        while (readBuf[0].hasRemaining())
            bytesTotal += ch.read(readBuf, 0, 1);


        if (bytesTotal > 0) {
            ProtocolHeader protocolHeader = null;
            try {
                readBuf[0].flip();
                protocolHeader = ProtocolUtils.checkHeaderIntegrity(readBuf[0]);

            } catch (Exception e) {
                log.severe("Check header integrity failed! Cause: " + e.getMessage());
            }

            readBuf[1] = ByteBuffer.allocate(ProtocolUtils.getBodySize(protocolHeader));

            while (readBuf[1].hasRemaining())
                bytesTotal += ch.read(readBuf, 1, 1);

            readBuf[1].flip();
            clientTMThreadMessagesCount.inc();
            onRead(readBuf[1]);
            readBuf[1].clear();
            readBuf[0].clear();

        } else if (bytesOp == -1) {
            log.info("peer closed read channel");
            ch.close();
        }
        clientTMThreadBytesRcv.inc(bytesTotal);
    }


    @Override
    public void run() {
        try {
            connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!Thread.currentThread().isInterrupted() && socketChannel.isOpen()) { // events multiplexing loop
            try {
                if (selector.select() > 0) processSelectedKeys(selector.selectedKeys());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        onDisconnected();
        writeBuf.clear();
        for (ByteBuffer byteBuffer : readBuf) {
            if (byteBuffer != null) byteBuffer.clear();
        }
        try {
            if (socketChannel != null)
                socketChannel.close();
            if (selector != null) selector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("connection closed");
    }

    public void disconnect() throws IOException {
        this.socketChannel.close();
        this.interrupt();
        this.selector.wakeup();

    }

    private static final int WRITE_BUFFER_SIZE = 0x100000;


    private ByteBuffer writeBuf = ByteBuffer.allocateDirect(WRITE_BUFFER_SIZE); // 1Mb

    public AtomicLong bytesSent = new AtomicLong();

    public void send(ByteBuffer buffer, int len) throws InterruptedException, IOException {

        synchronized (writeBuf) {
            buffer.flip();

            if (writeBuf.remaining() < buffer.remaining()) {
                writeBuf.flip();
                int bytesOp = 0, bytesTotal = 0;
                while (writeBuf.hasRemaining() && (bytesOp = socketChannel.write(writeBuf)) > 0) bytesTotal += bytesOp;
                writeBuf.compact();
                bytesSent.addAndGet(bytesTotal);
            }


            try {
                if (Thread.currentThread().getId() != this.getId()) {
                    while (writeBuf.remaining() < buffer.remaining())
                        writeBuf.wait();
                } else {
                    if (writeBuf.remaining() < buffer.remaining())
                        throw new IOException("send buffer full"); // TODO: add reallocation or buffers chain
                }
                writeBuf.put(buffer);

                if (buffer.remaining() > 0) {
                    writeBuf.put(buffer);
                    writeBuf.flip();
                    int bytesOp = 0, bytesTotal = 0;
                    while (writeBuf.hasRemaining() && (bytesOp = socketChannel.write(writeBuf)) > 0)
                        bytesTotal += bytesOp;
                    writeBuf.compact();
                    bytesSent.addAndGet(bytesTotal);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        /*    ByteBuffer buff1 = ByteBuffer.allocate(4);
            ByteBuffer buff2 = ByteBuffer.allocate(len);


            ByteBuffer[] bufferArray = new ByteBuffer[]{buff1, buff2};
            buff1.putInt(len);
            buff2.put(buffer);
            buff1.flip();
            buff2.flip();
            long totalSend = socketChannel.write(bufferArray);

            bytesSent.addAndGet(totalSend);
            if (totalSend < (len + 4))
                System.out.println("no" + totalSend);


            buff2.clear();
            buff2.clear();*/


            if (writeBuf.hasRemaining()) {
                SelectionKey key = socketChannel.keyFor(selector);
                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                selector.wakeup();
            }


        }
    }


    /**
     * Override with something meaningful
     *
     * @param buf
     */
    protected abstract void onRead(ByteBuffer buf) throws Exception;

    /**
     * Override with something meaningful
     *
     * @param buf
     */
    protected abstract void onConnected() throws Exception;

    /**
     * Override with something meaningful
     *
     * @param buf
     */
    protected abstract void onDisconnected();


}
