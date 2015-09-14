package com.hczhang.hummingbird.gateway;

import com.hczhang.hummingbird.command.Command;
import com.hczhang.hummingbird.command.annotation.Interceptor;
import com.hczhang.hummingbird.command.interceptor.CommandInterceptor;
import com.hczhang.hummingbird.command.interceptor.Dispatcher;
import com.hczhang.hummingbird.serializer.JsonSerializer;
import com.hczhang.hummingbird.serializer.Serializer;
import com.hczhang.hummingbird.util.AnnotationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by steven on 12/17/14.
 */
public class AnnotatedGateway extends AbstractGateway {

    private static Logger logger = LoggerFactory.getLogger(AnnotatedGateway.class);

    private String basePackage;

    private Dispatcher dispatcher;

    private ExecutorService pool;

    private Serializer serializer;

    public AnnotatedGateway() {
        pool = Executors.newCachedThreadPool();

        serializer = new JsonSerializer();
    }

    public void initial() {
        logger.info("Gateway is initializing..........................");

        this.scanInterceptor();

        logger.info("Gateway has been setup...........................");
    }

    private void scanInterceptor() {
        dispatcher = new Dispatcher(cloud);

        Set<Class<?>> classes = AnnotationUtils.findClasses(basePackage, Interceptor.class);
        Set<String> interceptors = new HashSet<String>();

        for (Class c : classes) {
            try {
                if (!CommandInterceptor.class.isAssignableFrom(c)) {
                    logger.error("The interceptor class [{}] must implement Interceptor.",
                            c.getSimpleName());
                    continue;
                }

                Interceptor i = (Interceptor) c.getAnnotation(Interceptor.class);
                CommandInterceptor ci = this.newInstance(i, c);

                dispatcher.addInterceptor(i.value(), ci);
                interceptors.add(c.getSimpleName());


            } catch (Exception e) {
                logger.warn("Cannot register Event Handler.", e);
            }
        }

        logger.info("Registered interceptors: [{}]", StringUtils.join(interceptors.iterator(), ","));

    }

    @Override
    void logCommand(Command command) {
        logger.info("Send a command [{}:({})] - {}", command.getClass().getSimpleName(),
                command.getAggregateID(), new String(serializer.serialize(command)));
    }

    @Override
    public Dispatcher getDispatcher() {
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

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public CommandInterceptor newInstance(Interceptor i, Class cls) throws Exception {
        return (CommandInterceptor) cls.newInstance();
    }
}

