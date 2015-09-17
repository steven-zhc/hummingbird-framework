package com.hczhang.hummingbird.cloud;


import com.hczhang.hummingbird.cloud.lifecycle.Lifecycle;
import com.hczhang.hummingbird.command.Command;
import com.hczhang.hummingbird.model.AggregateRoot;
import com.hczhang.hummingbird.transaction.TransactionContext;

/**
 * Cloud is core class. You could think about it as a context.
 * Everyone get object from {@code Cloud} , update it and save back to {@code Cloud} finally.
 *
 * Created by steven on 5/21/14.
 */
public interface Cloud extends Lifecycle {

    /**
     * Create a new transaction context, which will manage transaction and provide a sort of method to do it.
     * @return TransactionContext. transaction context
     */
    TransactionContext newTransaction();

    /**
     * Register a aggregate class as dew prototype in cloud.
     * This could be used in the future. <br>
     * which means the Aggregate (model) could be managemed by framework.
     * The framework will scan a class and find out all of
     * useful methods and constructors. <br>
     * Here is what we concern:
     * <ul>
     *     <li>Command Handler, including constructors and methods</li>
     *     <li>Event Handler</li>
     * </ul>
     * @param aggregateType aggregate root class type.
     */
    void registerDewPrototype(Class<? extends AggregateRoot> aggregateType);

    /**
     * Add domain service as a crystal of cloud.
     * That means the framework container has managed the object of domain service
     * @param service the service
     */
    void addCrystal(Object service);

    /**
     * Liquefy the element (Aggregate) from cloud.
     * You could get a aggregate according to parameter {@code id}.
     * This method will return null if there isn't aggregate referenced with id
     *
     * @param <T>  subclass of EventSourceAggregateRoot
     * @param context Transaction Context.
     * @param id aggregate id
     * @param type the type of aggregate
     * @return an instance of aggregate root.
     */
    public <T extends AggregateRoot> T liquefy(TransactionContext context, Object id, Class<T> type);

    /**
     * Liquefy the element (Aggregate) from cloud.
     * Compare with the other liquefy method. this one doesn't have transaction management.
     *
     * @param <T>  subclass of EventSourceAggregateRoot
     * @param id aggregate id
     * @param type the type of aggregate
     * @return an instance of aggregate root.
     */
    public <T extends AggregateRoot> T liquefy(Object id, Class<T> type);

    /**
     * Send a command to {@code Cloud}, which will invoke the proper method / constructor
     * on {@code AggregateRoot} or Domain Service.
     * This method will throw a AggregateIdInvalidException if target aid is invalid.
     * @param command Command message.
     * @return the result returned by command executing method.
     */
    Object vapor(Command command);

    /**
     * Spread a command to a concrete aggregate class, which locate in the {@code Cloud}.
     * For some special cases, the domain service need spread command to concrete class.
     *
     * @param <T>  concrete aggregate class.
     * @param context Transaction Context.
     * @param command Command
     * @param type aggregate class type, which will run the command
     * @return aggregate instance which executed the command.
     */
    <T extends AggregateRoot> T spread(TransactionContext context, Command command, Class<T> type);

    /**
     * Spread a command to a concrete aggregate class, which locate in the {@code Cloud}.
     * For some special cases, the domain service need spread command to concrete class.
     *
     * @param <T>  concrete aggregate class.
     * @param context Transaction Context.
     * @param command Command
     * @param obj aggregate object, which will run the command
     * @return aggregate instance which executed the command.
     */
    <T extends AggregateRoot> T spread(TransactionContext context, Command command, T obj);

}
