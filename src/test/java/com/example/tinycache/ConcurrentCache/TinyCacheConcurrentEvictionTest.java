package com.example.tinycache.ConcurrentCache;

import com.example.tinycache.TinyCacheConcurrent;
import com.example.tinycache.TinyCacheMetrics;
import com.example.tinycache.utils.Weigher;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class TinyCacheConcurrentEvictionTest {

    static final class ConstWeigher extends Weigher {
        private final int w; ConstWeigher(int w){this.w=w;} @Override public int weigh(Object value){return w;}
    }

    @Test
    void lruEvictionRespectsRecentAccess() {
        TinyCacheConcurrent<String,String> cache = new TinyCacheConcurrent<>(3, new ConstWeigher(1));
        cache.put("a","A");
        cache.put("b","B");
        cache.put("c","C");

        // touch a & b so that c becomes LRU
        assertEquals("A", cache.get("a"));
        assertEquals("B", cache.get("b"));

        cache.put("d","D"); // should evict c
        assertEquals(3, cache.size());
        assertThrows(NoSuchElementException.class, () -> cache.get("c"));

        TinyCacheMetrics m = cache.getMetrics();
        assertEquals(3, m.getEntryCount());
        assertEquals(2, m.getHits());
        assertEquals(1, m.getMisses()); // failed get(c)
        assertEquals(1, m.getEvictions());
        assertEquals(m.getEntryCount(), m.getTotalValueSize());
    }
}
