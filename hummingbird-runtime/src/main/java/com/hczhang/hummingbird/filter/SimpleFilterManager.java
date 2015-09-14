package com.hczhang.hummingbird.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by steven on 2/7/15.
 */
public class SimpleFilterManager extends AbstractFilterManager {

    private static Logger logger = LoggerFactory.getLogger(SimpleFilterManager.class);

    private Set<Filter> filters;

    public SimpleFilterManager() {
        this.filters = new HashSet<Filter>();
    }

    @Override
    protected Iterator<? extends Filter> getFilterIterator() {
        return filters.iterator();
    }

    @Override
    public void addFileter(Filter filter) {
        filters.add((Filter) filter);
    }

    @Override
    public void removeFilter(Filter filter) {
        filters.remove(filter);
    }

    public void setFilters(Set<Filter> filters) {
        this.filters = filters;
    }
}
