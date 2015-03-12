package io.turtle.jmx;


/**
 * Created by gabriele on 10/03/2015.
 */
public interface SystemConfigurationMXBean {
    public String getUpSince();


    public int getDispatchThreadCount();

    public int getSubscribeThreadCount();

    public int getPublishThreadCount();


}
