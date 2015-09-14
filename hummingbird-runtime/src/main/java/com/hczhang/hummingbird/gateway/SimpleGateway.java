package com.hczhang.hummingbird.gateway;

import com.hczhang.hummingbird.command.Command;
import com.hczhang.hummingbird.command.interceptor.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by steven on 12/17/14.
 */
public class SimpleGateway extends AbstractGateway {

    private static Logger logger = LoggerFactory.getLogger(AnnotatedGateway.class);

    private Dispatcher dispatcher;

    private ExecutorService pool;

    public SimpleGateway() {
        pool = Executors.newCachedThreadPool();
    }

    @Override
    public Dispatcher getDispatcher() {
        if (dispatcher == null) {
            dispatcher = new Dispatcher(cloud);
        }
        return dispatcher;
    }

    @Override
    void asyncSend(final Command command) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                cloud.vapor(command);
            }
        });
    }

    @Override
    void logCommand(Command command) {
        logger.info("Send a command [{}]", command.toString());
    }

}

