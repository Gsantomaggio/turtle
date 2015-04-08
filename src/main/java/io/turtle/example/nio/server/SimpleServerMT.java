package io.turtle.example.nio.server;

import com.codahale.metrics.ConsoleReporter;
import io.turtle.env.nio.server.ServerTurtleEnvironment;
import io.turtle.metrics.TMeter;
import io.turtle.metrics.impl.DropwizardTCounter;
import io.turtle.metrics.impl.DropwizardTMeter;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by gabriele on 22/03/15.
 */
public class SimpleServerMT {


    private static final Logger log = Logger.getLogger(SimpleServerMT.class.getName());


    public static void main(String[] args) throws InterruptedException, IOException {
        log.info("Starting Simple ServerMT");
        ServerTurtleEnvironment nioServerMT = new ServerTurtleEnvironment();
        nioServerMT.open();
        DropwizardTCounter debugCounter = new DropwizardTCounter();
        TMeter requests = new DropwizardTMeter();
        requests.register("DEBUG.Client.MessagesRate");
        debugCounter.register("DEBUG.Client.MessagesReceived");

       /* nioServerMT.subscribe((header, body, firstMatchTag) -> {
            //   log.info("Body message size:" + body.length + " - First tag match:" + firstMatchTag);
            requests.mark();
            debugCounter.inc();
        }, "#testtag");
*/

        ConsoleReporter reporter = ConsoleReporter.forRegistry(nioServerMT.getMetrics())
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(10, TimeUnit.SECONDS);
        log.info("press key to start test");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        reporter.stop();
        try {
            nioServerMT.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
