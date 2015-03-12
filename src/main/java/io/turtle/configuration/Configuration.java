package io.turtle.configuration;

/**
 * Created by gabriele on 08/03/15.
 */
public interface Configuration {

    int getDispatchThreadCount();

    int getSubscribeThreadCount();

    int getPublishThreadCount();

    int getWorkersThreadCount();


}
