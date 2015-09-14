package com.hczhang.hummingbird.command.interceptor;

import java.util.Comparator;

/**
 * Created by steven on 2/11/15.
 */
public class CommandComparator implements Comparator<Class<?>> {
    @Override
    public int compare(Class<?> o1, Class<?> o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
