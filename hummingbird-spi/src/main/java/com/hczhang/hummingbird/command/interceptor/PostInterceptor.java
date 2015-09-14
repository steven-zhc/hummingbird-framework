package com.hczhang.hummingbird.command.interceptor;


import com.hczhang.hummingbird.cloud.Cloud;
import com.hczhang.hummingbird.command.Command;

/**
 * Created by steven on 12/30/14.
 */
public abstract class PostInterceptor implements CommandInterceptor {

    @Override
    public Object preProcess(Cloud cloud, Dispatcher dispatcher, Command cmd) {
        return null;
    }
}
