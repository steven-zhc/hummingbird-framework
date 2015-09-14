package com.hczhang.hummingbird.command.interceptor;


import com.hczhang.hummingbird.cloud.Cloud;
import com.hczhang.hummingbird.command.Command;

/**
 * Created by steven on 12/29/14.
 */
public interface CommandInterceptor {

    /**
     * The constant CONTINUE_PRE_PROCESS.
     */
    public static final Object CONTINUE_PRE_PROCESS = null;
    /**
     * The constant CONTINUE_POST_PROCESS.
     */
    public static final boolean CONTINUE_POST_PROCESS = true;

    /**
     * This method will be executed by Dispatcher before command handler (which
     * was executed by cloud.vapor()). In normal case, you could do some initialize tasks.
     * You also could update the command.
     * @param cloud the cloud
     * @param dispatcher Dispatcher instance
     * @param cmd the command will be executed.
     * @return null, continue running the next interceptors and command handler; otherwise, return
     * a result object directly, and that will stop process and running target command handler.
     */
    public Object preProcess(Cloud cloud, Dispatcher dispatcher, Command cmd);

    /**
     * This method will be executed by Dispatcher after command handler (which
     * was executed by cloud.vapor()). You could update the result of command handler in this methods.
     * @param cloud the cloud
     * @param dispatcher Dispatcher instance
     * @param cmd Executed command
     * @param result the result of command execution.
     * @return true, continue running the next interceptors; otherwise, stop the process
     * and return the current result. the rest of interceptors will never be ran.
     * The order of interceptors are depend on the implementation of dispatcher.
     */
    public boolean postProcess(Cloud cloud, Dispatcher dispatcher, Command cmd, Object result);
}
