package com.hczhang.hummingbird.command.interceptor;

import java.util.Comparator;

/**
 * Created by steven on 2/11/15.
 */
public class InterceptorComparator implements Comparator<CommandInterceptor> {

    @Override
    public int compare(CommandInterceptor o1, CommandInterceptor o2) {
        int p1 = 0;
        if (o1 instanceof PriorityCommandInterceptor) {
            p1 = ((PriorityCommandInterceptor) o1).getPriority();
        }
        int p2 = 0;
        if (o2 instanceof PriorityCommandInterceptor) {
            p2 = ((PriorityCommandInterceptor) o2).getPriority();
        }

        return (p1 - p2) * -1;
    }
}
