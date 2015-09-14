package com.hczhang.hummingbird.util.collection;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by steven on 10/28/14.
 * @param <K>  the type parameter
 * @param <V>  the type parameter
 */
public class CopyOnWriteHashMap<K, V> implements Map<K, V> {

    private volatile Map<K, V> core;
    private volatile Map<K, V> view;

    private final AtomicBoolean requiresCopyOnWrite;

    /**
     * Instantiates a new Copy on write hash map.
     */
    public CopyOnWriteHashMap() {
        this.core = new HashMap<K, V>();
        this.requiresCopyOnWrite = new AtomicBoolean(false);
    }

    /**
     * Instantiates a new Copy on write hash map.
     *
     * @param that the that
     */
    public CopyOnWriteHashMap(CopyOnWriteHashMap<K, V> that) {
        this.core = that.core;
        this.requiresCopyOnWrite = new AtomicBoolean(true);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        try {
            return new CopyOnWriteHashMap(this);
        } finally {
            requiresCopyOnWrite.set(true);
        }
    }

    private void copy() {
        if (requiresCopyOnWrite.compareAndSet(true, false)) {
            core = new HashMap<K, V>(core);
            view = null;
        }
    }

    private Map<K, V> getView() {
        if (view == null) {
            view = Collections.unmodifiableMap(core);
        }
        return view;
    }

    @Override
    public int size() {
        return core.size();
    }

    @Override
    public boolean isEmpty() {
        return core.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return core.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return core.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return core.get(key);
    }

    @Override
    public V put(K key, V value) {
        copy();
        return core.put(key, value);
    }

    @Override
    public V remove(Object key) {
        copy();
        return core.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        copy();
        core.putAll(m);
    }

    @Override
    public void clear() {
        core = new HashMap<K, V>();
        view = null;
        copy();
    }

    @Override
    public Set<K> keySet() {
        return getView().keySet();
    }

    @Override
    public Collection<V> values() {
        return getView().values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return getView().entrySet();
    }

    @Override
    public String toString() {
        return core.toString();
    }
}
