[![Build Status](https://travis-ci.org/Gsantomaggio/turtle.svg?branch=master)](https://travis-ci.org/Gsantomaggio/turtle)
Thank you travis-ci.org

# Turtle
#####Why this name?
Because my son loves the ninja turtles.

#####What is ?
Turtle is a lightway publish and subscribe broker/library tag based...yes another one  !!

#####What does ?
With turtle you can publish and subscribe messages using “tags”.

#####Can I use it?
> The project is not complete yet, don't use it!!.

#####Simple example:
```java
        TurtleEnvironment env = new TurtleEnvironment();
        env.init();
        String subscriberId = env.subscribe(new MessagesHandler<Message>() {
            @Override
            public void handlerMessage(Message message) {
                 if (message instanceof StringMessage)
                    ((StringMessage) message).getValue();
            }
        },"#pizza","#pasta","#beer");
        
        StringMessage stringMessage = new StringMessage();
        stringMessage.setValue("today spaghetti and wine !!");
        env.publish( new StringMessage(),"#pasta","#wine","#spaghetti");
        ....
        env.unSubscribe(subscriberId);
        env.deInit();
```
#####About internals:
The library contains three threads kind:
- Publish-Thread
- Subscriber-Thread
- Worker-Thread

The Publish and the Subscribe threads are internal, by default their number is half of the core available for each category.
>es: 8 core, 4  for Publish-Thread and 4 for Subscriber-Thread.

The configuration can be modified.

The internals threads are not-blocking, they use LinkedBlockingQueue to share data between the threads but using pool with time-out.

The messages flow is described as follow:

![alt tag](https://raw.githubusercontent.com/Gsantomaggio/turtle/master/doc/images/Internals.jpg)

Each thread is isolated and can work without wait lock time between other threads. Internally the threads have an “dummy” cache to improve the performance in high throughput case.

The cache reduce the java synchronization time appending more than one message to the queue-list.  In this version the cache is not configurable and it is checked each 200 millisecond.

Finally the worker threads are used by clients,  if the pool is full the worker threads are blocked and the messages will be stored to the subscriber thread queue.



The “tag” engine is a simple map that contains the tag word and the subscriber-ids that use the tag.


#####Monitoring:
The current version implements JMX interface where it is possible to monitor the threads and the queues status, as following:

![alt tag](https://raw.githubusercontent.com/Gsantomaggio/turtle/master/doc/images/Configuration.png)

Resources count:

![alt tag](https://raw.githubusercontent.com/Gsantomaggio/turtle/master/doc/images/ResourcesCounter.png)




