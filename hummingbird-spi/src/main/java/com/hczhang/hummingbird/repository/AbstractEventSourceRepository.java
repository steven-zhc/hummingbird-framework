package com.hczhang.hummingbird.repository;

import com.codahale.metrics.Timer;
import com.hczhang.hummingbird.cloud.GenericEventSourceCloud;
import com.hczhang.hummingbird.event.Event;
import com.hczhang.hummingbird.event.EventConstants;
import com.hczhang.hummingbird.filter.FilterManager;
import com.hczhang.hummingbird.metrics.MetricsManager;
import com.hczhang.hummingbird.model.AggregateFactory;
import com.hczhang.hummingbird.model.EventSourceAggregateRoot;
import com.hczhang.hummingbird.model.exception.ModelRuntimeException;
import com.hczhang.hummingbird.util.DDDException;
import com.hczhang.hummingbird.util.HBAssert;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Queue;

import static com.codahale.metrics.MetricRegistry.name;


/**
 * Provide a default replay function to redo all events on object. And return finalized object.
 * The concrete Repository should extend this class, and provide serialize and de-serialize methods
 * by specific store strategy.
 * <br>
 * Created by steven on 4/30/14.
 */
public abstract class AbstractEventSourceRepository implements EventSourceRepository {

    private static Logger logger = LoggerFactory.getLogger(AbstractEventSourceRepository.class);

    private FilterManager filterManager;


    /**
     * The Hybird mode, which means this repository will retrieve aggregate through event source and QX query database togather.
     * Normarlly, we use hibrid mode to compatible with legacy system. <br>
     * False, this repository will retrieve object only by eventsource.
     */
    protected boolean hybird = false;

    /**
     * The constant START_INDEX.
     */
    protected static final long START_INDEX = -1;

    // Metrics
    private final Timer loadTimer = MetricsManager.metrics.timer(name(EventSourceRepository.class, "aggregate", "load", "timer"));
    private final Timer legacyLoadTimer = MetricsManager.metrics.timer(name(EventSourceRepository.class, "aggregate", "load", "legacy", "timer"));

    private final Timer loadEventTimer = MetricsManager.metrics.timer(name(EventSourceRepository.class, "event", "load", "timer"));
    private final Timer saveEventTimer = MetricsManager.metrics.timer(name(EventSourceRepository.class, "event", "save", "timer"));
    private final Timer replayEventTimer = MetricsManager.metrics.timer(name(EventSourceRepository.class, "event", "replay", "timer"));
    private final Timer saveSnapshotTimer = MetricsManager.metrics.timer(name(EventSourceRepository.class, "snapshot", "save", "timer"));
    private final Timer loadSnapshotTimer = MetricsManager.metrics.timer(name(EventSourceRepository.class, "snapshot", "load", "timer"));

    public boolean isHybird() {
        return hybird;
    }

    public void setHybird(boolean hybird) {
        this.hybird = hybird;
    }

    @Override
    public <T extends EventSourceAggregateRoot> T load(Object aid, Class<T> type, long version) {

        if (version == 0) {
            return load(aid, type);
        }

        T model = (T) retrieveSnapshot(aid, version);

        if (model != null) {
            // Aggregate = Snapshot + incremental events
            Queue<Event> es = this.loadEvents(aid, model.getVersion(), version);
            return this.replay(model, es);
        } else {
            Queue<Event> es = this.loadEvents(aid, START_INDEX, version);

            if (es.size() == 0) {
                return null;
            }

            try {
                T ar = type.newInstance();
                return this.replay(ar, es);
            } catch (Exception e) {
                logger.error("Cannot load aggregate<{}>[{}:{}].", type.getSimpleName(), aid.toString(), version, e);
                throw new RepositoryRuntimeException("Load aggregate exception. [{}]", e.getMessage());
            }
        }
    }

    @Override
    public <T extends EventSourceAggregateRoot> T load(Object aid, Class<T> type) {
        HBAssert.notNull(aid, ModelRuntimeException.class, "Aggregate id cannot be null");

        Timer.Context context = MetricsManager.getTimerContext(loadTimer);

        try {

            Timer.Context retrieveSnapshotContext = MetricsManager.getTimerContext(loadSnapshotTimer);

            EventSourceAggregateRoot model = null;
            try {
                model = retrieveSnapshot(aid);
            } finally {
                MetricsManager.stopTimerContext(retrieveSnapshotContext);
            }

            if (model == null) {

                if (hybird) {
                    // Load from legacy databse. e.g. mysql
                    return this.loadFromLegacyDB(aid, type);
                } else {
                    // load from event source
                    Queue<Event> es = this.loadEvents(aid);
                    if (es.size() == 0) {
                        return null;
                    }
                    try {
                        T ar = type.newInstance();
                        return this.replay(ar, es);
                    } catch (Exception e) {
                        logger.error("Cannot load aggregate<{}>[{}].", type.getSimpleName(), aid.toString(), e);
                        throw new RepositoryRuntimeException("Load aggregate exception. [{}]", e.getMessage());
                    }
                }

            } else {
                // Aggregate = Snapshot + incremental events
                Queue<Event> es = this.loadEvents(aid, model.getVersion());
                return (T) this.replay(model, es);
            }

        } finally {
            MetricsManager.stopTimerContext(context);
        }
    }

    private <T extends EventSourceAggregateRoot> T loadFromLegacyDB(Object id, Class<T> type) {

        AggregateFactory factory = null;
        if (type == null) {
            Queue<Event> es = this.loadEvents(id);
            if (es.isEmpty()) {
                throw new RepositoryRuntimeException("Cannot find any event of aid [{}]", id);
            }
            String className = es.peek().getMetaData(EventConstants.META_MODEL_TYPE);

            factory = GenericEventSourceCloud.getRadiator(className);
        } else {
            factory = GenericEventSourceCloud.getRadiator(type.getName());
        }

        if (factory == null) {
            logger.debug("Cannot find radiator (Bean Factory) for [{}].", type.getName());

            return null;
        }

        Timer.Context mysqlContext = MetricsManager.getTimerContext(legacyLoadTimer);
        try {
            EventSourceAggregateRoot m = (EventSourceAggregateRoot) factory.load(id);

            if (m != null) {
                m.updateVersion(this.getModelVersion(id));
            }

            return (T) m;

        } catch (DDDException e) {

            throw e;
        } catch (Exception e) {
            logger.error("Cannot load aggregate with id [{}]. Error<{}> msg:[{}]", id, e.getClass().getSimpleName(), e.getMessage());
            throw new RepositoryRuntimeException("Cannot load aggregate root[{}]", id);
        } finally {
            MetricsManager.stopTimerContext(mysqlContext);
        }
    }

    @Override
    public EventSourceAggregateRoot load(Object id) {

        return this.load(id, null);
    }

    @Override
    public EventSourceAggregateRoot refresh(EventSourceAggregateRoot model) {

        Validate.notNull(model, "model is null");

        try {
            Queue<Event> es = this.loadEvents(model.getAggregateID(), model.getVersion());
            return this.replay(model, es);
        } catch (Exception e) {
            logger.error(e.toString());
        }

        return null;


    }

    /**
     * Replay event source aggregate root.
     *
     * @param <T>  the type parameter
     * @param model the model
     * @param es the queue of event
     * @return the event source aggregate root
     */
    protected <T extends EventSourceAggregateRoot> T replay(T model, Queue<Event> es) {

        Validate.notNull(model, "Target model is null");

        if (es == null || es.isEmpty()) {
            logger.debug("Event stream is empty for aggregate [{}][{}]",
                    model.getClass().getSimpleName(), model.getAggregateID());
            return model;
        }

        Timer.Context context = MetricsManager.getTimerContext(replayEventTimer);

        try {

            for (Event event : es) {

                // Replay operations
                Method m = GenericEventSourceCloud.getEventHandler(event.getClass());
                m.setAccessible(true);

                try {

                    m.invoke(model, event);
                    model.updateVersion(event.getVersion());

                } catch (InvocationTargetException e) {

                    Throwable te = e.getTargetException();
                    logger.warn("Invoke {}.{}({}) got a target exception<{}>. Message: [{}]",
                            model.getClass().getSimpleName(),
                            m.getName(),
                            event.getClass().getSimpleName(),
                            te.getClass().getSimpleName(),
                            te.getMessage());

                    if (te instanceof RuntimeException) {
                        RuntimeException re = (RuntimeException) te;
                        throw re;
                    } else {
                        RepositoryRuntimeException me = new RepositoryRuntimeException(te.getMessage());
                        throw me;
                    }

                } catch (Exception e) {
                    logger.warn("Cannot invoke event handler {}.{}({}). Error<{}> message:{}",
                            model.getClass().getSimpleName(),
                            m.getName(),
                            event.getClass().getSimpleName(),
                            e.getClass().getSimpleName(),
                            e.getMessage()
                    );
                    throw new RepositoryRuntimeException("Replay events error.");
                }
            }
        } finally {
            MetricsManager.stopTimerContext(context);
        }

        return model;
    }

    @Override
    public void save(Event event) {

        Queue<Event> es = new LinkedList<Event>();
        es.add(event);

        save(es);

        if (logger.isDebugEnabled()) {
            logger.debug("Saved event [{}] on cassandra.", event.toString());
        }
    }

    @Override
    public void save(Queue<Event> eventStream) {
        if (eventStream == null) {
            logger.debug("EventStream is null");
            return;
        }

        Queue<Event> queue = new LinkedList<Event>();

        // Add filter
        if (filterManager != null) {

            for (Event e : eventStream) {
                if (filterManager.apply(e)) {
                    queue.offer(e);
                }
            }
        }

        if (eventStream.size() == 0) {
            return;
        }

        // Verify the version of model is new.
        // That means the model hasn't been updated since last load.
        Event e = eventStream.peek();
        long actVersion = e.getVersion();
        long targetVersion = this.getModelVersion(e.getAggregateID());

        if (targetVersion == EventConstants.VERSION_NULL) {
            logger.debug("Enable NullEventSourceRepository, that's means there is no event store.");
            return;
        }

        if (actVersion != (targetVersion + 1)) {
            logger.error("The aggregate has been updated since last load. Expected id [{}] + 1, Actually id [{}]", "" + targetVersion, "" + actVersion);
            throw new RepositoryRuntimeException("Aggregate [{}] data is dirty.", e.getAggregateID());
        }

        Timer.Context context = MetricsManager.getTimerContext(saveEventTimer);

        try {
            saveEvents(eventStream);
        } finally {
            MetricsManager.stopTimerContext(context);
        }

    }

    @Override
    public void takeSnapshot(EventSourceAggregateRoot model) {
        HBAssert.notNull(model, DDDException.class, "model must not be null");

        if (filterManager == null || filterManager.apply(model)) {

            Timer.Context context = MetricsManager.getTimerContext(saveSnapshotTimer);
            try {
                this.snapshot(model);
            } finally {
                MetricsManager.stopTimerContext(context);
            }
        }
    }

    /**
     * Snapshot void.
     *
     * @param model the model
     */
    protected abstract void snapshot(EventSourceAggregateRoot model);

    @Override
    public Queue<Event> loadEvents(Object id) {
        return this.loadEvents(id, START_INDEX);
    }

    @Override
    public Queue<Event> loadEvents(Object aid, long startVersion, long endVersion) {
        Timer.Context context = MetricsManager.getTimerContext(loadEventTimer);

        try {
            Queue<Event> el = queryEvents(aid, startVersion, endVersion);

            return el;
        } finally {
            MetricsManager.stopTimerContext(context);
        }
    }

    @Override
    public Queue<Event> loadEvents(Object id, long sinceVersion) {

        Timer.Context context = MetricsManager.getTimerContext(loadEventTimer);

        try {
            Queue<Event> el = queryEvents(id, sinceVersion);

            return el;
        } finally {
            MetricsManager.stopTimerContext(context);
        }
    }

    /**
     * Gets filter manager.
     *
     * @return the filter manager
     */
    public FilterManager getFilterManager() {
        return filterManager;
    }

    /**
     * Sets filter manager.
     *
     * @param filterManager the filter manager
     */
    public void setFilterManager(FilterManager filterManager) {
        this.filterManager = filterManager;
    }

    /**
     * Query events.
     *
     * @param aggregateID the aggregate iD
     * @param sinceVersion the since version
     * @return the list
     */
    protected abstract Queue<Event> queryEvents(Object aggregateID, long sinceVersion);

    /**
     * Query events. ({@code startVersion}, {@code endVersion} ].
     *
     * @param aggregateID the aggregate iD
     * @param startVersion the start version
     * @param endVersion the end version
     * @return the list
     */
    protected abstract Queue<Event> queryEvents(Object aggregateID, long startVersion, long endVersion);

    /**
     * Save events.
     *
     * @param eventStream the event stream
     */
    protected abstract void saveEvents(Queue<Event> eventStream);

}
