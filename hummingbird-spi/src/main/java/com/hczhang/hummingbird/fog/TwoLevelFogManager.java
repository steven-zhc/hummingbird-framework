package com.hczhang.hummingbird.fog;



import com.hczhang.hummingbird.model.AggregateRoot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by steven on 3/6/15.
 */
public class TwoLevelFogManager implements FogManager {

    private Fog level1;
    private Fog level2;

    private ExecutorService executorService;


    /**
     * Instantiates a new Two level fog manager.
     */
    public TwoLevelFogManager() {
        executorService = Executors.newCachedThreadPool();
    }

    /**
     * Instantiates a new Two level fog manager.
     *
     * @param level1 the level 1
     * @param level2 the level 2
     */
    public TwoLevelFogManager(Fog level1, Fog level2) {
        executorService = Executors.newCachedThreadPool();
        this.level1 = level1;
        this.level2 = level2;
    }

    @Override
    public void set(final AggregateRoot aggregateRoot) {
        if (level1 != null) {
            level1.addDew(aggregateRoot);
        }

        // update cache (level2) asynchronously
        if (level2 != null) {
            executorService.execute(new Runnable() {
                public void run() {
                    level2.addDew(aggregateRoot);
                }
            });
        }

    }

    @Override
    public AggregateRoot get(Object key) {

        if (level1 == null) {
            return null;
        }

        AggregateRoot ar = level1.getDew(key);

        if (ar != null) {
            return ar;
        } else if (level2 != null) {
            return level2.getDew(key);
        } else {
            return null;
        }
    }

    /**
     * Gets level 1.
     *
     * @return the level 1
     */
    public Fog getLevel1() {
        return level1;
    }

    /**
     * Sets level 1.
     *
     * @param level1 the level 1
     */
    public void setLevel1(Fog level1) {
        this.level1 = level1;
    }

    /**
     * Gets level 2.
     *
     * @return the level 2
     */
    public Fog getLevel2() {
        return level2;
    }

    /**
     * Sets level 2.
     *
     * @param level2 the level 2
     */
    public void setLevel2(Fog level2) {
        this.level2 = level2;
    }


}
