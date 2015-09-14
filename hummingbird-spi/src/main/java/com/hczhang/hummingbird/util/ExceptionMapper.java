package com.hczhang.hummingbird.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A util Class to analysis exception.
 * Created by steven on 10/23/14.
 */
public abstract class ExceptionMapper {

    private Map<Class<?>, String> mapper;

    /**
     * Instantiates a new Exception mapper.
     */
    protected ExceptionMapper() {
        mapper = new HashMap<Class<?>, String>();
        initMapper();
    }

    /**
     * Initial Mapper data.
     * Key is class of exception, the value is error code.
     */
    protected abstract void initMapper();

    /**
     * Default code.
     *
     * @return the string
     */
    protected String defaultCode() {
        return "103";
    }

    /**
     * Add item.
     *
     * @param exClass the ex class
     * @param code the code
     * @return the exception mapper
     */
    public ExceptionMapper addItem(Class<?> exClass, String code) {
        mapper.put(exClass, code);
        return this;
    }

    /**
     * Gets code.
     *
     * @param ex the ex
     * @return the code
     */
    public String getCode(Exception ex) {
        HBAssert.notNull(ex, "exception must not be null.");

        if (mapper.containsKey(ex.getClass())) {
            return mapper.get(ex.getClass());
        } else {
            return defaultCode();
        }
    }

}
