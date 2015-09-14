package com.hczhang.hummingbird.query;

/**
 * Created by steven on 8/27/14.
 */
public interface Criteria {

    /**
     * Build sql criteria which is include 'where' tag
     * @return where sql
     */
    public String build();

    /**
     * The criteria sql use '?' to stand for parameters
     * And this method will return the parameters array with correct order.
     * @return parameters object [ ]
     */
    Object[] params();
}
