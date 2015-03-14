package io.turtle.core.services.utils;

import java.util.concurrent.ThreadFactory;

/**
 * Created by gabriele on 14/03/15.
 */
public class TurtleThreadFactory implements ThreadFactory {

    String threadName;
    public TurtleThreadFactory(String threadName){
        this.threadName = threadName;

    }

    private int counter = 0;
    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, threadName +"-"+ counter++);
        return t;
    }

}
