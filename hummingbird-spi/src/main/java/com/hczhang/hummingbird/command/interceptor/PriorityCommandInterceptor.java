package com.hczhang.hummingbird.command.interceptor;

/**
 * Created by steven on 2/11/15.
 */
public interface PriorityCommandInterceptor extends CommandInterceptor {

    /**
     * Get a priority number of command interceptor.
     * The number is much greater, the priority is much higher,
     * and it will be executed before the lower number of interceptor
     * @return priority priority
     */
    int getPriority();
}
