package io.turtle.jmx;

import io.turtle.core.services.Resources;
import io.turtle.jmx.impl.ResourcesCounter;
import io.turtle.jmx.impl.SystemConfiguration;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * Created by gabriele on 10/03/2015.
 */
public class JMXAgent {

    private MBeanServer mbs = null;

    Resources resources;
    ResourcesCounter resourcesCounter;
    ObjectName systemConfigurationName = null;
    ObjectName resourcesCounterName = null;

    public ResourcesCounter getResourcesCounter() {
        return resourcesCounter;
    }

    public JMXAgent(Resources resources) {
        this.resources = resources;

    }

    public void init() {
        mbs = ManagementFactory.getPlatformMBeanServer();
        SystemConfiguration SystemConfiguration = new SystemConfiguration(resources);
        resourcesCounter = new ResourcesCounter(resources);

        try {

            systemConfigurationName = new ObjectName("Turtle:name=CurrentConfiguration");
            resourcesCounterName = new ObjectName("Turtle:name=ResourceCounter");
            mbs.registerMBean(SystemConfiguration, systemConfigurationName);
            mbs.registerMBean(resourcesCounter, resourcesCounterName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deInit() {
        try {
            mbs.unregisterMBean(systemConfigurationName);
            mbs.unregisterMBean(resourcesCounterName);
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        }


    }


}


