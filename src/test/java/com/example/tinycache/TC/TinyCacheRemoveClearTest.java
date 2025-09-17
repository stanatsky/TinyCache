package com.example.tinycache.TC;

import com.example.tinycache.TinyCache;
import com.example.tinycache.TinyCacheMetrics;
import com.example.tinycache.utils.Weigher;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class TinyCacheRemoveClearTest {

    static final class ConstWeigher extends Weigher {
        @Override public int weigh(Object value) { return 10; }
    }

    @Test
    void removeEntryAndAdjustMetrics() {
        TinyCache<String,String> cache = new TinyCache<>(5, new ConstWeigher());
        cache.put("x","X");
        cache.put("y","Y");
        assertEquals(2, cache.size());

        String removed = cache.remove("x");
        assertEquals("X", removed);
        assertEquals(1, cache.size());

        assertThrows(NoSuchElementException.class, () -> cache.remove("x"));

        TinyCacheMetrics m = cache.getMetrics();
        assertEquals(1, m.getEntryCount());
        assertEquals(0, m.getHits());
        assertEquals(0, m.getMisses());
    }

    @Test
    void clearEmptiesCacheAndResetsSizeBytes() {
        TinyCache<String,String> cache = new TinyCache<>(5, new ConstWeigher());
        cache.put("a","A");
        cache.put("b","B");
        cache.clear();
        assertEquals(0, cache.size());
        TinyCacheMetrics m = cache.getMetrics();
        assertEquals(0, m.getEntryCount());
        assertEquals(0, m.getTotalValueSize());
    }
}
