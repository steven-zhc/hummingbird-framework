package com.hczhang.hummingbird.eventlog;

import com.hczhang.hummingbird.cloud.GenericEventSourceCloud;
import com.hczhang.hummingbird.event.Event;
import com.hczhang.hummingbird.event.EventConstants;
import com.hczhang.hummingbird.model.AggregateRoot;
import com.hczhang.hummingbird.repository.AggregateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In CQRS principle, we need a component could sync data to query database when a Command/event was fired.
 * In Hummingbird, we use an event log to do this. And this is QXSyncEventLog main responsibility.
 *
 * Created by steven on 1/5/15.
 */
public class QXSyncEventLog extends AbstractEventLog {

    private static Logger logger = LoggerFactory.getLogger(QXSyncEventLog.class);

    @Override
    public void recordEvent(Event event, AggregateRoot root) {
        Object id = event.getAggregateID();
        String t = event.getMetaData(EventConstants.META_MODEL_TYPE);
        try {

            Class type = Class.forName(t);

            if (root == null) {
                logger.warn("Cannot get aggregate<{}>({})", type.getSimpleName(), id.toString());
            } else if (root.getVersion() == event.getVersion()) {

                AggregateRepository repository = GenericEventSourceCloud.getRepository(type);

                if (repository != null) {
                    if (root.isDeleted()) {
                        repository.delete(root);
                    } else if (repository.exists(id)) {
                        repository.update(root);
                    } else {
                        repository.save(root);
                    }
                } else {
                    logger.warn("Missing repository configuration [{}]", type.getSimpleName());
                }
            }
        } catch (ClassNotFoundException e) {
            logger.error("Cannot find aggregate class [{}], error message [{}]", t, e.getMessage());
        } catch (Exception e) {
            logger.error("SQL Exception: ", e);
        }
    }

}
