package com.hczhang.hummingbird.repository;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.hczhang.hummingbird.event.Event;
import com.hczhang.hummingbird.event.EventConstants;
import com.hczhang.hummingbird.eventsource.EventSourceException;
import com.hczhang.hummingbird.model.EventSourceAggregateRoot;
import com.hczhang.hummingbird.model.exception.ModelRuntimeException;
import com.hczhang.hummingbird.serializer.JsonSerializer;
import com.hczhang.hummingbird.serializer.Serializer;
import com.hczhang.hummingbird.util.HBAssert;
import com.hczhang.ostrich.BatchPreparedStatementSetter;
import com.hczhang.ostrich.CassandraTemplate;
import com.hczhang.ostrich.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by steven on 10/26/14.
 */
public class CassandraEventSourceRepository extends AbstractEventSourceRepository {

    private static final Logger logger = LoggerFactory.getLogger(CassandraEventSourceRepository.class);

    private Serializer serializer = new JsonSerializer();

    private CassandraTemplate template;

    public CassandraTemplate getTemplate() {
        return template;
    }

    public void setTemplate(CassandraTemplate template) {
        this.template = template;
    }

    @Override
    protected Queue<Event> queryEvents(Object aggregateID, long sinceVersion) {
        String cql = "select * from events where aid = ? and version > ? order by version asc";

        List<Event> list = template.queryForList(cql, new EventRowMapper(), aggregateID.toString(), new Long(sinceVersion));

        return new LinkedList<Event>(list);
    }

    @Override
    protected Queue<Event> queryEvents(Object aid, long startVersion, long endVersion) {
        HBAssert.notNull(aid, ModelRuntimeException.class, "AggregateID must not be null");

        String cql = "select * from events where aid = ? and version > ? and version <= ? order by version asc";

        List<Event> list = template.queryForList(cql, new EventRowMapper(), aid.toString(), new Long(startVersion), new Long(endVersion));

        return new LinkedList<Event>(list);
    }

    @Override
    protected void saveEvents(final Queue<Event> eventStream) {
        String cql = "insert into events (aid, tid, ctime, version, body, meta) values (?, ?, ?, ?, ?, ?)";
        final int eventSize = eventStream.size();
        final Iterator<Event> it = eventStream.iterator();

        template.batchExec(cql, new BatchPreparedStatementSetter() {
            @Override
            public BoundStatement setValues(PreparedStatement ps, int i) {
                Event e = it.next();
                if (e != null) {
                    return ps.bind(e.getAggregateID().toString(), e.getCommandID(), e.getTimestamp(), e.getVersion(),
                            new String(serializer.serialize(e)), e.getMetaData());
                } else {
                    return null;
                }
            }
            @Override
            public int getBatchSize() {
                return eventSize;
            }
        });
    }

    @Override
    public long getModelVersion(Object aggid) {
        HBAssert.notNull(aggid, ModelRuntimeException.class, "AggregateID must not be null");

        String cql = "select version from events where aid = ? order by version desc limit 1";

        return template.queryForLong(cql, aggid.toString());
    }

    @Override
    public EventSourceAggregateRoot retrieveSnapshot(Object aggregateID) {
        HBAssert.notNull(aggregateID, ModelRuntimeException.class, "AggregateID must not be null");

        String cql = "select * from snapshot where aid = ? limit 1";

        return template.queryForObject(cql, new SnapshotRowMapper(), aggregateID.toString());
    }

    @Override
    public EventSourceAggregateRoot retrieveSnapshot(Object aid, long version) {
        HBAssert.notNull(aid, ModelRuntimeException.class, "AggregateID must not be null");

        String cql = "select * from snapshot where aid = ? and version <= ? limit 1";

        return template.queryForObject(cql, new SnapshotRowMapper(), aid.toString(), version);
    }

    @Override
    public void dropSnapshot(final Object aid, long version) {
        HBAssert.notNull(aid, ModelRuntimeException.class, "AggregateID must not be null");

        // TODO: should delete later. workaround cassandra issue
        // https://issues.apache.org/jira/browse/CASSANDRA-6237

        String cql = "select version from snapshot where aid = ? and version > ?";
        final List<Long> vs = template.queryForList(cql, new RowMapper<Long>() {
            @Override
            public Long mapRow(Row row, int rowNum) {
                return row.getLong("version");
            }
        }, aid, version);

        cql = "delete from snapshot where aid = ? and version = ?";

        template.batchExec(cql, new BatchPreparedStatementSetter() {
            @Override
            public BoundStatement setValues(PreparedStatement ps, int i) {
                Long v = vs.get(i);
                return ps.bind(aid, v);
            }

            @Override
            public int getBatchSize() {
                return vs.size();
            }
        });
    }

    @Override
    protected void snapshot(EventSourceAggregateRoot root) {
        HBAssert.notNull(root, ModelRuntimeException.class, "Aggregate root must not be null");

        String cql = "insert into snapshot (aid, mt, ctime, version, body) values (?, ?, ?, ?, ?)";

        if (root.getVersion() < EventConstants.SNAPSHOT_FACTOR) {
            template.execute(cql, root.getAggregateID().toString(), root.getClass().getName(),
                    new java.util.Date().getTime(), root.getVersion(),
                    new String(serializer.serialize(root)));
        } else {
            template.executeAsync(cql, root.getAggregateID().toString(), root.getClass().getName(),
                    new java.util.Date().getTime(), root.getVersion(),
                    new String(serializer.serialize(root)));
        }
    }

    @Override
    public void dropEvents(final Object aid, long version) {
        HBAssert.notNull(aid, ModelRuntimeException.class, "AggregateID must not be null");

        // TODO: should delete later. workaround cassandra issue
        // https://issues.apache.org/jira/browse/CASSANDRA-6237
        String cql = "select version from events where aid = ? and version > ?";
        final List<Long> vs = template.queryForList(cql, new RowMapper<Long>() {
            @Override
            public Long mapRow(Row row, int rowNum) {
                return row.getLong("version");
            }
        }, aid, version);

        cql = "delete from events where aid = ? and version = ?";

        template.batchExec(cql, new BatchPreparedStatementSetter() {
            @Override
            public BoundStatement setValues(PreparedStatement ps, int i) {
                Long v = vs.get(i);
                return ps.bind(aid, v);
            }

            @Override
            public int getBatchSize() {
                return vs.size();
            }
        });
    }

    private class EventRowMapper implements RowMapper<Event> {

        @Override
        public Event mapRow(Row row, int rowNum) {
            byte[] body = row.getString("body").getBytes();

            Map<String, String> meta = row.getMap("meta", String.class, String.class);

            String eventType = meta.get(EventConstants.META_EVENT_TYPE);
            try {
                Class classType = Class.forName(eventType);
                Event event = (Event) serializer.deserialize(body, classType);

                return event;
            } catch (ClassNotFoundException e) {
                throw new EventSourceException("Cannot find class [{}]", eventType);
            }
        }
    }

    private class SnapshotRowMapper implements RowMapper<EventSourceAggregateRoot> {

        @Override
        public EventSourceAggregateRoot mapRow(Row row, int rowNum) {
            byte[] body = row.getString("body").getBytes();
            String modelType = row.getString(EventConstants.META_MODEL_TYPE);

            try {
                Class classType = Class.forName(modelType);
                EventSourceAggregateRoot model = (EventSourceAggregateRoot) serializer.deserialize(body, classType);

                return model;
            } catch (ClassNotFoundException e) {
                throw new EventSourceException("Cannot find class [{}]", modelType);
            }
        }
    }
}
