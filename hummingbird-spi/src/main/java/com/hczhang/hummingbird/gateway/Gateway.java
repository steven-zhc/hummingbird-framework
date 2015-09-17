package com.hczhang.hummingbird.gateway;


import com.hczhang.hummingbird.command.Command;

/**
 * This interface represent for a gateway of command.
 * It will be a connection between {@code Command} and {@code Cloud}.
 * The basic idea is we want to separate the Command and Cloud.
 * The Command producer doesn't need to know where and who will received this command.
 * <p>
 * Another normal case we want to use {@code Gateway} is we want to control the command system.
 * For example, the current transaction cannot be executed successful, as the framework
 * throw exception or there is a conflict on data model. we could decide the strategy on handling this issue.
 * Retry or discard this transaction. That's up to you.
 *
 * Created by steven on 5/21/14.
 *
 * @since 1.0
 */
public interface Gateway {

    /**
     * Send a command to cloud. and wait for the response of command execution.
     * @param <T>  return type.
     * @param command will be executed by cloud
     * @param respType class type of return object
     * @return A object will be affected by command, normally it is aggregate.
     */
    <T> T send(Command command, Class<T> respType);

    /**
     * Send a command to cloud. and wait for the response of command execution.
     * @param command will be executed by cloud
     * @return A object will be affected by command, normally it is aggregate.
     */
    Object send(Command command);

    /**
     * This is a async method of {@code send}
     * @param command will be executed by cloud.
     */
    void sendAndForget(Command command);

}
