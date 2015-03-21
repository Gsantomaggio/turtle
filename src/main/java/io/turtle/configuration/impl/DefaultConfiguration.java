package io.turtle.configuration.impl;

import io.turtle.configuration.Configuration;

/**
 * Created by gabriele on 08/03/15.
 */
public class DefaultConfiguration implements Configuration {

    int dispatchThreadCount = 0;
    int subscribeThreadCount = 0;
    int publishThreadCount = 0;
    int workersThreadCount = 50;


    public DefaultConfiguration() {
        dispatchThreadCount = Runtime.getRuntime().availableProcessors();
        switch (dispatchThreadCount) {
            case 1:
            case 2: {
                subscribeThreadCount = 1;
                publishThreadCount = 1;
            }
            default: {
                publishThreadCount = (dispatchThreadCount / 2);
                subscribeThreadCount = (dispatchThreadCount / 2);

            }
        }
            // for test, it will be removed
        dispatchThreadCount = publishThreadCount + subscribeThreadCount;
    }


    @Override
    public int getDispatchThreadCount() {
        return dispatchThreadCount;
    }

    @Override
    public int getSubscribeThreadCount() {
        return subscribeThreadCount;
    }

    @Override
    public int getPublishThreadCount() {
        return publishThreadCount;
    }

    @Override
    public int getWorkersThreadCount() {
        return workersThreadCount;
    }


}
