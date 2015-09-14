package com.hczhang.hummingbird.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * Created by steven on 2/6/15.
 */
public abstract class AbstractFilterManager implements FilterManager {

    private static Logger logger = LoggerFactory.getLogger(AbstractFilterManager.class);

    private String type = "";

    @Override
    public boolean apply(Object content) {

        Iterator<? extends Filter> it = getFilterIterator();

        while (it.hasNext()) {
            Filter filter = it.next();

            if (!filter.passes(content)) {

                logger.info("Content [{}] cannot pass '{}' filter [{}]",
                        content.getClass().getSimpleName(), type, filter.getClass().getSimpleName());
                return false;
            }
        }
        return true;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets filter iterator.
     *
     * @return the filter iterator
     */
    protected abstract Iterator<? extends Filter> getFilterIterator();
}
