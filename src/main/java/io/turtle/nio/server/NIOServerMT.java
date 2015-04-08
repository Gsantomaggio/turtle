package io.turtle.nio.server;

import io.turtle.core.routing.RoutingMessage;
import io.turtle.core.services.utils.TurtleThreadFactory;
import io.turtle.metrics.TMeter;
import io.turtle.metrics.impl.DropwizardTCounter;
import io.turtle.metrics.impl.DropwizardTMeter;
import io.turtle.nio.server.protocol.ProtocolUtils;


import java.io.IOException;
import java.nio.ByteBuffer;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by gabriele on 22/03/15.
 */
public abstract class NIOServerMT {
    ExecutorService nioServerPool;
    NIOServerTMThread nioServerTMThread;

    public abstract void onReadRoutingMessage(RoutingMessage routingMessage,SelectionKey key);
    public abstract void onSubscribeRoutingMessage(RoutingMessage routingMessage,SelectionKey key);
    public abstract void onServerRemoveClient(SelectionKey key);


    public void startServer() {


        TMeter messageBeforeDispatch = new DropwizardTMeter();
        DropwizardTCounter counterCallback = new DropwizardTCounter();
        counterCallback.register("NIOServerMT.CounterCallBack");
        messageBeforeDispatch.register("NIOServerMT.MessagesBeforeDispatch");
        nioServerPool = Executors.newSingleThreadExecutor(new TurtleThreadFactory("nioServer"));
        nioServerTMThread = new NIOServerTMThread() {
            @Override
            protected void onPublish(ByteBuffer buf,SelectionKey key) throws Exception {
                messageBeforeDispatch.mark();
                counterCallback.inc();
                try {
                    RoutingMessage routingMessage = ProtocolUtils.getRoutingMessage(buf);
                    onReadRoutingMessage(routingMessage,key);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            protected void onRegisterSubscribe(ByteBuffer buf,SelectionKey key) throws Exception {
                RoutingMessage routingMessage = ProtocolUtils.getRoutingMessage(buf);
                buf.clear();
                onSubscribeRoutingMessage(routingMessage,key);


            }

            @Override
            protected void onRemoveClient(SelectionKey key) {
                onServerRemoveClient(key);

            }
        };
        nioServerPool.submit(nioServerTMThread);
    }

    public void stopServer() {
        try {
            nioServerTMThread.stopServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        nioServerPool.shutdown();
    }


}
