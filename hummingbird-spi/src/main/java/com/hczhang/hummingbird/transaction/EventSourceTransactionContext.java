package com.hczhang.hummingbird.transaction;


import com.hczhang.hummingbird.cloud.EventSourceCloud;
import com.hczhang.hummingbird.event.Event;
import com.hczhang.hummingbird.event.EventConstants;
import com.hczhang.hummingbird.eventbus.EventRouter;
import com.hczhang.hummingbird.eventlog.EventLog;
import com.hczhang.hummingbird.fog.FogManager;
import com.hczhang.hummingbird.model.AggregateRoot;
import com.hczhang.hummingbird.model.EventSourceAggregateRoot;
import com.hczhang.hummingbird.model.exception.ModelRuntimeException;
import com.hczhang.hummingbird.repository.EventSourceRepository;
import com.hczhang.hummingbird.util.HBAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by steven on 4/3/15.
 */
public class EventSourceTransactionContext implements TransactionContext {

    private static Logger logger = LoggerFactory.getLogger(EventSourceTransactionContext.class);

    private EventSourceCloud cloud;

    private EventSourceRepository repository;

    private EventRouter router;

    private Set<EventLog> eventLogs;

    private FogManager fogManager;

    private Set<EventSourceAggregateRoot> managedBean;

    /**
     * Instantiates a new Event source transaction context.
     *
     * @param cloud the cloud
     */
    public EventSourceTransactionContext(EventSourceCloud cloud) {
        HBAssert.notNull(cloud, ModelRuntimeException.class, "Cloud must not be null.");

        managedBean = new HashSet<EventSourceAggregateRoot>();

        this.cloud = cloud;

        this.repository = cloud.getEventSourceRepository();
        this.router = cloud.getEventRouter();
        this.eventLogs = cloud.getEventLogs();
        this.fogManager = cloud.getFogManager();
    }

    @Override
    public void govern(AggregateRoot root) {
        if (root instanceof EventSourceAggregateRoot) {
            managedBean.add((EventSourceAggregateRoot) root);
        } else {
            logger.warn("The aggregate root [{}] should be subclass of EventSourceAggregateRoot", root.getClass().getSimpleName());
        }
    }

    @Override
    public Set<? extends AggregateRoot> getBeans() {
        return managedBean;
    }

    @Override
    public void commit() {
        Queue<Event> eventStream = new LinkedList<Event>();
        List<EventSourceAggregateRoot> archiveList = new ArrayList<EventSourceAggregateRoot>();

        for (EventSourceAggregateRoot root : managedBean) {

            Queue<Event> stream = root.getUncommittedEvents();
            for (Event event : stream) {

                // decide if we should take snapshot
                if (event.getVersion() == 1 || event.getVersion() % EventConstants.SNAPSHOT_FACTOR == 0) {
                    archiveList.add(root);
                }

                eventStream.add(event);
            }

        }

        // save to event store
        this.getRepository().save(eventStream);

        // take snapshot
        for (EventSourceAggregateRoot root : archiveList) {
            this.getRepository().takeSnapshot(root);
        }

        // propagate events
        this.getRouter().propagate(eventStream);

        for (EventSourceAggregateRoot root : managedBean) {

            // call event log
            try {
                for (EventLog el : this.getEventLogs()) {
                    if (root.getUncommittedEvents() == null) {
                        logger.info("Event Log Service: uncommittedEvents is null");
                    }
                    el.log(root.getUncommittedEvents(), root);
                }
            } catch (Exception e) {
                // TODO check exception: Event Log Service throw out an exception: [NullPointerException:null]
                // requestURL:[/usermanagement/user/email/r.davies082@btinternet.com/password/d6ac47db-f11d-41ac-b7f2-678408155578]
                // serviceName:[updatePasswordByUserKey]
                logger.error("Event Log Service throw out an exception: ", e);
            }

            // clean up events in aggregate.
            root.purgeEvents();

            // add aggregate root to cache
            this.getFogManager().set(root);
        }


    }

    /**
     * Gets cloud.
     *
     * @return the cloud
     */
    public EventSourceCloud getCloud() {
        return cloud;
    }

    /**
     * Gets event logs.
     *
     * @return the event logs
     */
    public Set<EventLog> getEventLogs() {
        return eventLogs;
    }

    /**
     * Gets repository.
     *
     * @return the repository
     */
    public EventSourceRepository getRepository() {
        return repository;
    }

    /**
     * Gets router.
     *
     * @return the router
     */
    public EventRouter getRouter() {
        return router;
    }

    /**
     * Gets fog manager.
     *
     * @return the fog manager
     */
    public FogManager getFogManager() {
        return fogManager;
    }
}
