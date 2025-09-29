package com.example.tinycache.ConcurrentCache;

import com.example.tinycache.TinyCacheConcurrent;
import com.example.tinycache.TinyCacheMetrics;
import com.example.tinycache.utils.Weigher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TinyCacheConcurrentInvalidArgsTest {

    static final class DummyWeigher extends Weigher { @Override public int weigh(Object value){ return 1; } }

    @Test
    void constructorRejectsNonPositiveCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new TinyCacheConcurrent<>(0, new DummyWeigher()));
    }

    @Test
    void nullKeyOrValueRejected() {
        TinyCacheConcurrent<String,String> cache = new TinyCacheConcurrent<>(2, new DummyWeigher());
        assertThrows(IllegalArgumentException.class, () -> cache.put(null, "X"));
        assertThrows(IllegalArgumentException.class, () -> cache.put("k", null));
        assertThrows(IllegalArgumentException.class, () -> cache.get(null));
        assertThrows(IllegalArgumentException.class, () -> cache.remove(null));
    }

    @Test
    void getUnknownKeyThrowsAndCountsMiss() {
        TinyCacheConcurrent<String,String> cache = new TinyCacheConcurrent<>(2, new DummyWeigher());
        assertThrows(Exception.class, () -> cache.get("absent"));
        TinyCacheMetrics m = cache.getMetrics();
        assertEquals(0, m.getHits());
        assertEquals(1, m.getMisses());
    }
}

