package io.turtle.jmx;

/**
 * Created by gabriele on 10/03/15.
 */
public interface ResourcesCounterMXBean {

    public int getSubscriberCount();

    public int getTagIndexCount();

    public int getTotalMessagesDeliveredByWorker();

    public int getTotalMessagesPublished();

    public int getTotalMessagesDelivered();

}
