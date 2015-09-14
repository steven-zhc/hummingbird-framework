package com.hczhang.hummingbird.command.interceptor;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import com.hczhang.hummingbird.cloud.Cloud;
import com.hczhang.hummingbird.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by steven on 12/29/14.
 */
public class Dispatcher {

    private static Logger logger = LoggerFactory.getLogger(Dispatcher.class);

    private Cloud cloud;

    private Multimap<Class<? extends Command>, CommandInterceptor> interceptors;

    /**
     * Instantiates a new Dispatcher.
     *
     * @param cloud the cloud
     */
    public Dispatcher(Cloud cloud) {

        this.cloud = cloud;

        interceptors = TreeMultimap.create(new CommandComparator(), new InterceptorComparator());
    }


    /**
     * Intercept object.
     *
     * @param cmd the cmd
     * @return the object
     */
// TODO: Add finalized style of command interceptor
    public Object intercept(Command cmd) {

        Object result = null;
        for (CommandInterceptor inter : interceptors.get(cmd.getClass())) {
            logger.debug("Pre-Process interceptor [{}]", inter.getClass().getSimpleName());
            result = inter.preProcess(this.cloud, this, cmd);
            if (result != null) {
                return result;
            }
        }

        result = cloud.vapor(cmd);

        for (CommandInterceptor inter : interceptors.get(cmd.getClass())) {
            logger.debug("Post-Process interceptor [{}]", inter.getClass().getSimpleName());
            boolean flag = inter.postProcess(this.cloud, this, cmd, result);
            if (flag == false) {
                return result;
            }
        }

        return result;
    }

    /**
     * Add interceptor.
     *
     * @param cmdType the cmd type
     * @param interceptor the interceptor
     */
    public void addInterceptor(Class<? extends Command> cmdType, CommandInterceptor interceptor) {
        interceptors.put(cmdType, interceptor);
    }

    /**
     * Remove interceptor.
     *
     * @param cmdType the cmd type
     * @param interceptor the interceptor
     */
    public void removeInterceptor(Class<? extends Command> cmdType, CommandInterceptor interceptor) {
        interceptors.get(cmdType).remove(interceptor);
    }
}
