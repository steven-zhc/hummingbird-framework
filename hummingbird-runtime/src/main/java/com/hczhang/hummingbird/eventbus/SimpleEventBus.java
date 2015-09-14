package com.hczhang.hummingbird.eventbus;

import com.hczhang.hummingbird.event.Event;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * This is simple EventBus implement.
 * Created by steven on 3/24/14.
 */
public class SimpleEventBus extends AbstractEventBus {

    private static Logger logger = LoggerFactory.getLogger(SimpleEventBus.class);

    private final LinkedBlockingQueue<Event> bus;

    private ExecutorService pool;
    private Future<Boolean> status;

    private Class<? extends Event> eventType;

    public SimpleEventBus(Class<? extends Event> eventType) {
        bus = new LinkedBlockingQueue<Event>();

        this.eventType = eventType;

        pool = Executors.newSingleThreadExecutor();
        status = pool.submit(new EventTask());

    }

    @Override
    public Class getEventType() {
        return eventType;
    }

    @Override
    public void publish(Event event) {

        Validate.notNull(event, "Event is null");

        if (getListeners().size() == 0) {
            return;
        }

        try {
            bus.put(event);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public boolean startup() {
        return true;
    }

    @Override
    public boolean shutdown() {
        try {
            return status.get();
        } catch (Exception e) {
            logger.error("We got shutdown error: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public long getBusSize() {
        return this.bus.size();
    }


    class EventTask implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {
            logger.debug("Event Bus<{}> [{}] is running ...", eventType.getSimpleName(), SimpleEventBus.class.getSimpleName());

            while (!pool.isShutdown()) {

                Event event = bus.poll(2l, TimeUnit.SECONDS);
                if (event != null) {
                    logger.debug("{} handler(s) is(are) listening to event [{}].", getListeners().size(), event.getClass().getSimpleName());

                    handle(event);
                }
            }
            logger.debug("Event Bus<{}> stopped ...", eventType.getSimpleName());
            return true;
        }
    }
}
