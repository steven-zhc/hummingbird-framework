package com.hczhang.hummingbird.filter;

import java.util.Set;

/**
 * Created by steven on 2/6/15.
 */
public interface FilterManager {

    /**
     * Apply boolean.
     *
     * @param content the content
     * @return the boolean
     */
    boolean apply(Object content);

    /**
     * Add fileter.
     *
     * @param filter the filter
     */
    void addFileter(Filter filter);

    /**
     * Remove filter.
     *
     * @param filter the filter
     */
    void removeFilter(Filter filter);

    /**
     * Sets filters.
     *
     * @param filters the filters
     */
    public void setFilters(Set<Filter> filters);

}
