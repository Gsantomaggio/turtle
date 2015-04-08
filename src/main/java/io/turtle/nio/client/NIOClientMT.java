package io.turtle.nio.client;


import io.turtle.core.routing.RoutingMessage;
import io.turtle.nio.server.protocol.ProtocolUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Created by gabriele on 22/03/15.
 */
public abstract class NIOClientMT {
    private static final Logger log = Logger.getLogger(NIOClientMT.class.getName());
    ExecutorService nioServerPool;
    public NIOClientTMThread nioClientTMThread;

    public abstract void onReadRoutingMessage(RoutingMessage message);
    public void connect() {
        nioServerPool = Executors.newSingleThreadExecutor();
        nioClientTMThread = new NIOClientTMThread() {
            @Override
            protected void onRead(ByteBuffer buf) throws Exception {

                onReadRoutingMessage(ProtocolUtils.getRoutingMessage(buf));
            }

            @Override
            protected void onConnected() throws Exception {
                log.info("onConnected");
            }

            @Override
            protected void onDisconnected() {
                log.info("onDisconnected");

            }
        };
        nioServerPool.submit(nioClientTMThread);
    }


    public void disconnect() throws IOException {
        nioClientTMThread.disconnect();
        nioServerPool.shutdownNow();
    }

    public AtomicInteger atomicLong = new AtomicInteger();

    public synchronized void publish(Map<String, String> header, byte[] body, String... tags) throws InterruptedException, IOException {
        AtomicInteger size = new AtomicInteger();
        ByteBuffer publishBuffer = ProtocolUtils.writePublishMessage(header, body, atomicLong, size, tags);
        nioClientTMThread.send(publishBuffer, size.get());
    }


    public synchronized void subscribe(String... tags) throws InterruptedException, IOException {
        AtomicInteger size = new AtomicInteger();
        ByteBuffer subscribeBuffer = ProtocolUtils.writeSubscribe(atomicLong, size, tags);
        nioClientTMThread.send(subscribeBuffer, size.get());
    }



}
