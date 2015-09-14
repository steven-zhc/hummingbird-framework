package com.hczhang.hummingbird.gateway;

import com.hczhang.hummingbird.cloud.Cloud;
import com.hczhang.hummingbird.cloud.CloudRuntimeException;
import com.hczhang.hummingbird.command.Command;
import com.hczhang.hummingbird.command.interceptor.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by steven on 12/9/14.
 */
public abstract class AbstractGateway implements Gateway {

    private static final Logger log = LoggerFactory.getLogger(AbstractGateway.class);

    protected Cloud cloud;

    /**
     * Gets dispatcher.
     *
     * @return the dispatcher
     */
    public abstract Dispatcher getDispatcher();


    public void setCloud(Cloud cloud) {
        this.cloud = cloud;
    }

    /**
     * Gets cloud.
     *
     * @return the cloud
     */
    public Cloud getCloud() {
        return cloud;
    }

    @Override
    public Object send(Command command) {
        logCommand(command);
        return getDispatcher().intercept(command);
    }

    @Override
    public <T> T send(Command command, Class<T> respType) {
        Object obj = this.send(command);

        if (obj != null && respType.isInstance(obj) ) {
            return (T) obj;
        } else {
            log.error("Wrong class type of return. expected is [{}], but we got [{}].",
                    respType.getSimpleName(), obj.getClass().getSimpleName());
            throw new CloudRuntimeException("Got a wrong return type.");
        }

    }

    @Override
    public void sendAndForget(Command command) {
        logCommand(command);
        asyncSend(command);
    }

    /**
     * Async send.
     *
     * @param command the command
     */
    abstract void asyncSend(Command command);

    /**
     * Log command.
     *
     * @param command the command
     */
    abstract void logCommand(Command command);
}


