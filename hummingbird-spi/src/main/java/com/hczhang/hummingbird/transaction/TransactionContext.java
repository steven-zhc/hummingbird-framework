package com.hczhang.hummingbird.transaction;


import com.hczhang.hummingbird.model.AggregateRoot;

import java.util.Set;

/**
 * Created by steven on 4/6/15.
 */
public interface TransactionContext {

    /**
     * Govern void.
     *
     * @param root the root
     */
    void govern(AggregateRoot root);

    /**
     * Gets beans.
     *
     * @return the beans
     */
    Set<? extends AggregateRoot> getBeans();

    /**
     * Commit void.
     */
    void commit();
}
