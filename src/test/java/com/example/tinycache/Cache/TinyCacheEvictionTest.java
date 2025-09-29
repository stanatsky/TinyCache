package com.example.tinycache.Cache;

import com.example.tinycache.TinyCache;
import com.example.tinycache.TinyCacheMetrics;
import com.example.tinycache.utils.Weigher;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class TinyCacheEvictionTest {

    static final class ConstWeigher extends Weigher {
        private final int w;
        ConstWeigher(int w) { this.w = w; }
        @Override public int weigh(Object value) { return w; }
    }

    @Test
    void lruEvictionOnCapacityExceeded() {
        TinyCache<String, String> cache = new TinyCache<>(3, new ConstWeigher(1));
        cache.put("a","A");
        cache.put("b","B");
        cache.put("c","C");

        // Touch 'a' so it becomes most recently used
        assertEquals("A", cache.get("a"));

        // Add 'd' -> should evict least recently used (b)
        cache.put("d","D");
        assertEquals(3, cache.size());

        // 'b' should be gone
        assertThrows(NoSuchElementException.class, () -> cache.get("b"));

        TinyCacheMetrics m = cache.getMetrics();
        assertEquals(3, m.getEntryCount());
        assertEquals(1, m.getHits());
        assertEquals(1, m.getMisses()); // the failed get(b)
        assertEquals(1, m.getEvictions());
    }
}
