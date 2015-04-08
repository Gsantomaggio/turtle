package io.turtle.env.nio.server;

import io.turtle.core.handlers.MessagesHandler;
import io.turtle.core.routing.RoutingMessage;
import io.turtle.core.tag.impl.ServerTagIndex;
import io.turtle.env.TurtleEnvironment;
import io.turtle.metrics.TCounter;
import io.turtle.metrics.impl.DropwizardTCounter;
import io.turtle.nio.server.NIOServerMT;
import io.turtle.nio.server.protocol.ProtocolUtils;
import io.turtle.pubsub.impl.nio.NioSubscriber;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by gabriele on 23/03/15.
 */
public class ServerTurtleEnvironment extends TurtleEnvironment<SelectionKey> {
    class ServerMessagesHandler implements MessagesHandler<SelectionKey> {


        @Override
        public void handleMessage(Map<String, String> header, byte[] body, String firstMatchTag, SelectionKey sourceSubscriber) {
            ByteBuffer byteBuffer = ProtocolUtils.writePublishMessage(header, body, null, null, new String[]{firstMatchTag});
            byteBuffer.flip();
            serverTurtleSendBackMessages.inc();
            SocketChannel socketChannel = (SocketChannel) sourceSubscriber.channel();
            while (byteBuffer.hasRemaining())
                try {
                    socketChannel.write(byteBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            byteBuffer.clear();


        }
    }


    private static final Logger log = Logger.getLogger(ServerTurtleEnvironment.class.getName());
    TCounter serverSubscriberCount = new DropwizardTCounter();


    TCounter serverTurtleSendBackMessages = new DropwizardTCounter();
    NIOServerMT nioServerMT;

    @Override
    public synchronized void open() {
        ServerMessagesHandler serverMessagesHandler = new ServerMessagesHandler();
        tagIndex = new ServerTagIndex();
        super.open();
        log.info("Starting Simple ServerMT");
        serverSubscriberCount.register("NIOServerMT.ServerSubscribersCount");
        serverTurtleSendBackMessages.register("NIOServerMT.serverTurtleSendBackMessages");
        nioServerMT = new NIOServerMT() {
            @Override
            public void onReadRoutingMessage(RoutingMessage routingMessage, SelectionKey key) {
                //   try {

                try {
                    publish(routingMessage);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
/*                    tmpSelectionKeySent.clear();
                    for (String stag : routingMessage.getTags()) {
                        for (SelectionKey selectionKey : serverTagIndex.getSubscriberIdsByTag(stag)) {
                            if (tmpSelectionKeySent.indexOf(selectionKey)<0) {
                                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                                List<String> list = routingMessage.getTags();
                                String[] array = list.toArray(new String[list.size()]);
                                ByteBuffer byteBuffer = ProtocolUtils.writePublishMessage(routingMessage.getHeader(), routingMessage.getBody(), null, null, array);
                                byteBuffer.flip();
                                serverTurtleSendBackMessages.inc();
                                while (byteBuffer.hasRemaining())
                                    socketChannel.write(byteBuffer);
                                byteBuffer.clear();
                                tmpSelectionKeySent.add(selectionKey);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }


            @Override
            public void onSubscribeRoutingMessage(RoutingMessage routingMessage, SelectionKey key) {
                NioSubscriber nioSubscriber = new NioSubscriber();
                nioSubscriber.setSubscribeId(key);
                nioSubscriber.getMessageHandlers().add(serverMessagesHandler);
                List<String> list = routingMessage.getTags();
                String[] array = list.toArray(new String[list.size()]);

                subscribe(nioSubscriber,array);
                serverSubscriberCount.inc();


            }

            @Override
            public void onServerRemoveClient(SelectionKey key) {
                unSubscribe(key);
                serverSubscriberCount.dec();


            }
        };
        nioServerMT.startServer();
    }


    @Override
    public synchronized void close() throws IOException {
        nioServerMT.stopServer();
        super.close();
    }


}
