package io.turtle.test;

import io.turtle.env.local.LocalTurtleEnvironment;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by gabriele on 10/03/15.
 */
public class InternalCountTest extends BaseTestClass {

    @Test
    public void tesInternalCount() throws Exception {
        LocalTurtleEnvironment localTurtleEnvironment = new LocalTurtleEnvironment();
        localTurtleEnvironment.open();
        int subCount;
        String subid1 = localTurtleEnvironment.subscribe((header, body, firstMatchTag,sourceSubscriber) -> {

        }, "tag1", "tag2", "tag3", "tag4", "tag5", "tag6");

   //     subCount = localTurtleEnvironment.getSubscribersCount();
        String subid2 = localTurtleEnvironment.subscribe((header, body, firstMatchTag,sourceSubscriber) -> {

        }, "tag6", "tag7", "tag8", "tag9", "tag10");


        assertTrue(localTurtleEnvironment.getTagIndexCount() == 10);
        subCount = localTurtleEnvironment.getSubscribersCount();
        assertTrue(subCount == 2);

        localTurtleEnvironment.unSubscribe(subid1);
        localTurtleEnvironment.unSubscribe(subid2);
        assertTrue(localTurtleEnvironment.getTagIndexCount() == 0);
        assertTrue(localTurtleEnvironment.getSubscribersCount() == 0);

        localTurtleEnvironment.close();
    }
}
