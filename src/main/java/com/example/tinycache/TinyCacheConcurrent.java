package com.example.tinycache;

import com.example.tinycache.utils.Cache;
import com.example.tinycache.utils.CacheEntry;
import com.example.tinycache.utils.Weigher;

import java.util.LinkedHashMap;
import java.util.NoSuchElementException;

public class TinyCacheConcurrent<K, V> implements Cache<K, V> {

    private final LinkedHashMap<K, CacheEntry<V>> map;
    private final int maxEntries;
    private final Weigher weigher;

    private long hits;
    private long misses;
    private long evictions;
    private long totalValueSize;

    public TinyCacheConcurrent(int maxEntries, Weigher weigher) {
        if (maxEntries <= 0) {
            throw new IllegalArgumentException("maxEntries must be > 0");
        }
        this.maxEntries = maxEntries;
        this.weigher = weigher;

        this.map = new LinkedHashMap<>(maxEntries, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<K, CacheEntry<V>> eldest) {
                boolean evict = size() > TinyCacheConcurrent.this.maxEntries;
                if (evict) {
                    evictions++;
                    totalValueSize -= eldest.getValue().getApproxWeight();
                }
                return evict;
            }
        };
    }

    @Override
    public V get(K key) {
        if (key == null) throw new IllegalArgumentException("key must be non-null");
        synchronized (this) {
            CacheEntry<V> entry = map.get(key);
            if (entry == null) {
                misses++;
                throw new NoSuchElementException("No entry found for key: " + key);
            }
            hits++;
            return entry.getValue();
        }
    }

    @Override
    public void put(K key, V value) {
        if (key == null || value == null) throw new IllegalArgumentException("key and value must not be null");
        synchronized (this) {
            int weight = weigher.weigh(value);
            CacheEntry<V> newEntry = new CacheEntry<>(value, weight);
            CacheEntry<V> previous = map.put(key, newEntry);
            if (previous != null) {
                totalValueSize -= previous.getApproxWeight();
            }
            totalValueSize += newEntry.getApproxWeight();
        }
    }

    @Override
    public V remove(K key) {
        if (key == null) throw new IllegalArgumentException("key must not null");
        synchronized (this) {
            CacheEntry<V> removed = map.remove(key);
            if (removed == null) throw new NoSuchElementException("No entry found for key: " + key);
            totalValueSize -= removed.getApproxWeight();
            return removed.getValue();
        }
    }

    @Override
    public int size() {
        synchronized (this) {
            return map.size();
        }
    }

    @Override
    public TinyCacheMetrics getMetrics() {
        synchronized (this) {
            return new TinyCacheMetrics(
                    map.size(),
                    hits,
                    misses,
                    evictions,
                    totalValueSize
            );
        }
    }

    @Override
    public void clear() {
        synchronized (this) {
            map.clear();
            totalValueSize = 0L;
        }
    }
}