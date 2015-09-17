package com.hczhang.hummingbird.command;

/**
 * A command interface.
 *
 * @param <ID>  Aggregate ID type
 * Created by steven on 4/25/14.
 */
public interface Command<ID> {
    /**
     * This mehtod will return unique id of command.
     * In some case, we could think it as a transaction id.
     * @return Command ID.
     */
    public String getCommandID();

    /**
     * The method will return the unique id (Aggregate ID) of {@code IModel} Object.
     * That mean the command will do operation on that model.
     * Some time we call it target Aggregate ID.
     * <p>
     * Sometime Aggregate ID could be null if this is initial command. That means
     * this command will create new {@code IModel}. Otherwise the aggregate ID must not be null.
     * @return Target Aggregate ID.
     */
    public ID getAggregateID();
}
