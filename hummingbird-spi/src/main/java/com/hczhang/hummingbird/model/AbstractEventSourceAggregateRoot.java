package com.hczhang.hummingbird.model;

import com.hczhang.hummingbird.cloud.GenericEventSourceCloud;
import com.hczhang.hummingbird.event.Event;
import com.hczhang.hummingbird.event.EventConstants;
import com.hczhang.hummingbird.model.exception.ModelRuntimeException;
import com.hczhang.hummingbird.util.DDDException;
import com.hczhang.hummingbird.util.ModelUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * An abstract class of EventSourceAggregateRoot.
 * Created by steven on 3/24/14.
 * @param <ID>  the type parameter
 */
public abstract class AbstractEventSourceAggregateRoot<ID> implements EventSourceAggregateRoot<ID> {

    private static Logger logger = LoggerFactory.getLogger(AbstractEventSourceAggregateRoot.class);

    /**
     * An stream of un-committed events.
     */
    protected transient Queue<Event> uncommitEvents;

    /**
     * Aggregate version. the version will be increased for every event apply
     */
    protected long version;

    /**
     * An indicator if the Aggregate has been deleted.
     * True means this aggregate has been deleted. otherwise is false.
     */
    protected boolean isDelete;

    /**
     * Instantiates a new Abstract event source aggregate root.
     */
    protected AbstractEventSourceAggregateRoot() {
        this.init();
    }

    /**
     * Initial methods.
     * TODO: cache use AggregateRoot for constructor; can not call this init()
     */
    protected void init() {
        uncommitEvents = new LinkedBlockingQueue();
        version = 0;
        isDelete = false;
    }

    /**
     * The client shouldn't call this method directly.
     */
    @Override
    public void updateVersion(long v) {
        this.version = v;
    }

    @Override
    public Queue<Event> getUncommittedEvents() {
        return this.uncommitEvents;
    }

    @Override
    public void purgeEvents() {
        this.uncommitEvents.clear();
    }

    @Override
    public void applyEvent(Event event) {

        if (isDelete) {
            logger.warn("The Aggregate has been deleted. The event will not execute :[{}].", event.toString());
            return;
        }

        // update Aggregate version
        version++;
        event.setVersion(this.version);

        // add Aggregate type
        event.withMetaData(EventConstants.META_MODEL_TYPE, this.getClass().getName());
        // add event type
        event.withMetaData(EventConstants.META_EVENT_TYPE, event.getClass().getName());

        logger.info("Applying event({}) on [{}] - {}",
                event.getClass().getSimpleName(), this.getClass().getSimpleName(), event.toString());

        // add event into un-committed list
        uncommitEvents.offer(event);

        // Replay operations
        try {
            this.executeEventMethod(event);
        } catch (InvocationTargetException e) {

            Throwable te = e.getTargetException();
            logger.warn("The Aggregate Root [{}] got a target exception<{}>. Message: [{}]",
                    getClass().getName(),
                    te.getClass().getSimpleName(),
                    te.getMessage());

            if (te instanceof DDDException) {
                DDDException re = (DDDException) te;
                throw re;
            } else {

                ModelRuntimeException me = new ModelRuntimeException("Apply Event got exception<{}> [{}].",
                        te.getClass().getSimpleName(),
                        te.getMessage()
                );

                throw me;
            }

        } catch (Exception e) {
            logger.error("The Aggregate [" + getClass().getName() + "] got an exception.", e);
        }

    }

    /**
     * Execute event method.
     *
     * @param event the event
     * @throws InvocationTargetException the invocation target exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected void executeEventMethod(Event event) throws InvocationTargetException, IllegalAccessException {
        Validate.notNull(event, "Event cannot be null");

        Method m = GenericEventSourceCloud.getEventHandler(event.getClass());

        if (m == null) {
            logger.warn("Cannot find {}.({}) handler. Please call cloud.recognizeDew() method for better performance.",
                    getClass().getSimpleName(), event.getClass().getSimpleName());

            m = ModelUtils.getOneMethodWithParam(getClass(), event.getClass());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Invoke method {}.{}({}).", this.getClass().getSimpleName(), m.getName(),
                    event.getClass().getSimpleName());
        }

        m.setAccessible(true);
        m.invoke(this, event);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AggregateRoot root = (AggregateRoot) o;

        return new EqualsBuilder()
                .append(getAggregateID(), root.getAggregateID())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getAggregateID())
                .toHashCode();
    }

    @Override
    public long getVersion() {
        return this.version;
    }

    @Override
    public boolean isDeleted() {
        return this.isDelete;
    }

    /**
     * Sets delete.
     *
     */
    public void delete() {
        this.isDelete = true;
    }
}
