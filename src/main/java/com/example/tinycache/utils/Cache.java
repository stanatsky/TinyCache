package com.example.tinycache.utils;

import com.example.tinycache.TinyCacheMetrics;

public interface Cache<K, V> {
    V get(K key);
    void put(K key, V value);
    V remove(K key);
    int size();
    TinyCacheMetrics getMetrics();
    void clear();
}
