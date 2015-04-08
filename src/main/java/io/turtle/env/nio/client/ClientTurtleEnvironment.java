package io.turtle.env.nio.client;

import io.turtle.core.handlers.MessagesHandler;
import io.turtle.core.routing.RoutingMessage;
import io.turtle.env.local.LocalTurtleEnvironment;
import io.turtle.nio.client.NIOClientMT;

import java.io.IOException;
import java.util.Map;

/**
 * Created by gabriele on 31/03/2015.
 */
public class ClientTurtleEnvironment extends LocalTurtleEnvironment {

    private NIOClientMT nioClientMT;
    private ClientTurtleEnvironment owner = this;

    public ClientTurtleEnvironment() {
        super();
        nioClientMT = new NIOClientMT() {
            @Override
            public void onReadRoutingMessage(RoutingMessage message) {
                try {
                    owner.publish(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    @Override
    public void open() {
        super.open();
        nioClientMT.connect();
    }

    @Override
    public void close() throws IOException {
        super.close();
        nioClientMT.disconnect();
    }



    public synchronized void remotePublish(Map<String, String> header, byte[] body, String... tags) throws InterruptedException, IOException {
        nioClientMT.publish(header, body, tags);
    }


    public void remotePublish(byte[] body, String... tags) throws InterruptedException, IOException {
        this.remotePublish(null, body, tags);
    }


    public synchronized String remoteSubscribe(MessagesHandler messageHandler, String... tags) throws IOException, InterruptedException {
        nioClientMT.subscribe(tags);
        return super.subscribe(messageHandler, tags);

    }


}
