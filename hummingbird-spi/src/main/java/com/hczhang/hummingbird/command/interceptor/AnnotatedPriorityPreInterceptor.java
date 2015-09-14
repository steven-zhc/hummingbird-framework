package com.hczhang.hummingbird.command.interceptor;


import com.hczhang.hummingbird.cloud.Cloud;
import com.hczhang.hummingbird.command.Command;
import com.hczhang.hummingbird.command.annotation.Interceptor;

/**
 * Created by steven on 12/30/14.
 */
public abstract class AnnotatedPriorityPreInterceptor implements PriorityCommandInterceptor {

    @Override
    public int getPriority() {

        Interceptor interceptor = getClass().getAnnotation(Interceptor.class);

        if (interceptor == null) {
            return 0;
        }

        return interceptor.priority();
    }

    @Override
    public boolean postProcess(Cloud cloud, Dispatcher dispatcher, Command cmd, Object result) {
        return true;
    }
}
