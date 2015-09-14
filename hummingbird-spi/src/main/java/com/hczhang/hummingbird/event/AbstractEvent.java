package com.hczhang.hummingbird.event;

import org.apache.commons.lang3.builder.*;

import java.util.*;

/**
 * Created by steven on 5/29/14.
 * @param <ID>  the type parameter
 */
public abstract class AbstractEvent<ID> implements Event<ID> {

    /**
     * The Id.
     */
    protected String id;
    /**
     * The Timestamp.
     */
    protected long timestamp;
    /**
     * The Version.
     */
    protected long version;
    /**
     * The Meta data.
     */
    protected Map<String, String> metaData;

    /**
     * The Command iD.
     */
    protected String commandID;

    /**
     * Instantiates a new Abstract event.
     */
    public AbstractEvent() {
        this.timestamp = System.currentTimeMillis();
        this.id = UUID.randomUUID().toString();
        metaData = new HashMap();
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String getCommandID() {
        return this.commandID;
    }

    /**
     * Sets command iD.
     *
     * @param commandID the command iD
     */
    public void setCommandID(String commandID) {
        this.commandID = commandID;
    }

    public long getVersion() {
        return version;
    }

    @Override
    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public Map<String, String> getMetaData() {
        return metaData;
    }

    @Override
    public Event withMetaData(Map<String, String> metaData) {
        for (String key : metaData.keySet()) {
            this.metaData.put(key.toLowerCase(), metaData.get(key));
        }
        return this;
    }

    @Override
    public Event withMetaData(String key, String value) {

        metaData.put(key.toLowerCase(), value);
        return this;
    }

    @Override
    public String getMetaData(String key) {
        return metaData.get(key.toLowerCase());
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("commandID", commandID)
                .append("timestamp", timestamp)
                .append("version", version)
                .append("metaData", metaData)
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        AbstractEvent rhs = (AbstractEvent) obj;
        return new EqualsBuilder()
                .append(this.id, rhs.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .toHashCode();
    }


}
