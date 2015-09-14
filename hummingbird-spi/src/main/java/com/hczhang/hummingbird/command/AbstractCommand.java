package com.hczhang.hummingbird.command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by steven on 5/31/14.
 * @param <ID>   the type parameter
 */
public abstract class AbstractCommand<ID> implements Command<ID> {

    /**
     * Unique ID of command
     */
    protected String commandID;

    /**
     * The Env.
     */
    protected Map<String, String> env;

    /**
     * Instantiates a new Abstract command.
     */
    protected AbstractCommand() {
        commandID = UUID.randomUUID().toString();
        env = new HashMap<String, String>();
    }

    @Override
    public String getCommandID() {
        return this.commandID;
    }

    /**
     * With env.
     *
     * @param env the env
     */
    public void withEnv(Map<String, String> env) {
         this.env.putAll(env);
    }

    /**
     * With env.
     *
     * @param key the key
     * @param value the value
     */
    public void withEnv(String key, String value) {
        this.env.put(key, value);
    }

    /**
     * Gets env.
     *
     * @return the env
     */
    public Map<String, String> getEnv() {
        return env;
    }

    /**
     * Gets env.
     *
     * @param key the key
     * @return the env
     */
    public String getEnv(String key) {
        return env.get(key);
    }
}
