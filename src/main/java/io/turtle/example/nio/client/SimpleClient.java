package io.turtle.example.nio.client;



import io.turtle.env.nio.client.ClientTurtleEnvironment;
import io.turtle.nio.client.NIOClientMT;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by gabriele on 23/03/2015.
 */
public class SimpleClient {

    private static final Logger log = Logger.getLogger(SimpleClient.class.getName());



    public static void main(String[] args) throws InterruptedException {

        class  SendThread extends Thread{

            ClientTurtleEnvironment nioClientMT;
            public  SendThread(ClientTurtleEnvironment nioClientMT){
                this.nioClientMT = nioClientMT;
            }

            @Override
            public void  run(){
                byte[] byteTosend = new byte[1*1024];
                try {
                    for (int k = 0; k < 2/*200000*/; k++) {
                        nioClientMT.publish(null, byteTosend, "#testtag");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

        log.info("Starting Simple client");
        List<ClientTurtleEnvironment> nioClientMTs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ClientTurtleEnvironment nioClientMT = new ClientTurtleEnvironment();
            nioClientMT.open();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            nioClientMTs.add(nioClientMT);
        }

        long startTime = System.currentTimeMillis();
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (ClientTurtleEnvironment clientMT : nioClientMTs) {
            threadPool.submit( new SendThread(clientMT));
        }

        log.info("press key to stop");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();


        threadPool.shutdown();
        threadPool.awaitTermination(10, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        log.info("1 - **** Duration:" + duration);
        nioClientMTs.forEach(x -> {
            try {
                x.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }
}
