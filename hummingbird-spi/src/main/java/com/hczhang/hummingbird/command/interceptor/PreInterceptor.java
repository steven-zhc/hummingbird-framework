package com.hczhang.hummingbird.command.interceptor;


import com.hczhang.hummingbird.cloud.Cloud;
import com.hczhang.hummingbird.command.Command;

/**
 * Created by steven on 12/30/14.
 */
public abstract class PreInterceptor implements CommandInterceptor {
    @Override
    public boolean postProcess(Cloud cloud, Dispatcher dispatcher, Command cmd, Object result) {
        return true;
    }
}
