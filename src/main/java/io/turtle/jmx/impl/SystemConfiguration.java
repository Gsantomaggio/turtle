package io.turtle.jmx.impl;

import io.turtle.core.services.Resources;
import io.turtle.jmx.SystemConfigurationMXBean;

import java.util.Date;

/**
 * Created by gabriele on 10/03/2015.
 */
public class SystemConfiguration implements SystemConfigurationMXBean {
    private String upSince = new Date().toString();

    public String getUpSince() {
        return upSince;
    }

    Resources resources;

    public SystemConfiguration(Resources resources) {
        this.resources = resources;

    }


    @Override
    public int getDispatchThreadCount() {
        return resources.getCurrentConfiguration().getDispatchThreadCount();
    }

    @Override
    public int getSubscribeThreadCount() {
        return resources.getCurrentConfiguration().getSubscribeThreadCount();
    }

    @Override
    public int getPublishThreadCount() {
        return resources.getCurrentConfiguration().getPublishThreadCount();
    }


}
