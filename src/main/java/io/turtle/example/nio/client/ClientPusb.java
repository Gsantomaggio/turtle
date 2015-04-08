package io.turtle.example.nio.client;


import com.codahale.metrics.ConsoleReporter;
import io.turtle.env.nio.client.ClientTurtleEnvironment;
import io.turtle.metrics.TCounter;
import io.turtle.metrics.impl.DropwizardTCounter;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by gabriele on 01/04/2015.
 */
public class ClientPusb {
    private static final Logger log = Logger.getLogger(ClientPusb.class.getName());

    public static void main(String[] args) throws InterruptedException, IOException {

        TCounter messagesReceived = new DropwizardTCounter();
        messagesReceived.register("DEBUG.ClientPusb.MessagesReceiced");
        ClientTurtleEnvironment nioClientMT = new ClientTurtleEnvironment();
        nioClientMT.open();
        Thread.sleep(500);
        System.out.println("Client Connected");
        System.out.println("1- subscribe");
        System.out.println("2- publish");
        System.out.println("3- both");
        Scanner scanner = new Scanner(System.in);
        int value = 3;
        try {
            value = scanner.nextInt();
        } catch (Exception e) {
            value = 3;
        }

        switch (value) {
            case 1: {
                System.out.println("Remote Subscribe");
                try {
                    nioClientMT.remoteSubscribe((header, body, firstMatchTag,sourceSubscriber) -> {
                        log.info(" ClientTurtleEnvironment " + new String(body) + " - firstMatch" + firstMatchTag);
                        messagesReceived.inc();
                    }, "testtag");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

            case 2: {
                try {
                    System.out.println("pub count");
                    value = scanner.nextInt();
                } catch (Exception e) {
                    value = 1;
                }

                System.out.println("Remote Publish");
                for (int i = 0; i < value; i++) {
                    nioClientMT.remotePublish("ciao".getBytes(), "testtag");
                }
                break;
            }
        }

        ConsoleReporter reporter = ConsoleReporter.forRegistry(nioClientMT.getMetrics())
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(10, TimeUnit.SECONDS);


        log.info("press key to stop");
        scanner = new Scanner(System.in);
        scanner.nextLine();
        reporter.close();
        nioClientMT.close();
    }
}
