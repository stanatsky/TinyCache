package com.example.tinycache.utils;

public class CacheEntry<V> {
    private final V value;
    private final int weight;

    public CacheEntry(V value, int weight) {
        this.value = value;
        this.weight = weight;
    }

    public V getValue() {
        return value;
    }

    public int getApproxWeight() {
        return weight;
    }

}
