package com.example.tinycache.ConcurrentCache;

import com.example.tinycache.TinyCacheConcurrent;
import com.example.tinycache.TinyCacheMetrics;
import com.example.tinycache.utils.Weigher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TinyCacheConcurrentUpdateWeightTest {
    @Test
    void updatingExistingKeyReplacesWeight_usingByteWeigher() {
        Weigher weigher = new Weigher();
        TinyCacheConcurrent<String, String> cache = new TinyCacheConcurrent<>(100_000, weigher); // big capacity

        String sShort = "A";
        String sLong = "ABCDEFGHIJ"; // longer string

        int wShort = weigher.weigh(sShort);
        int wLong = weigher.weigh(sLong);
        assertTrue(wLong > wShort, "Longer string should weigh more");

        cache.put("k", sShort);
        TinyCacheMetrics m1 = cache.getMetrics();
        assertEquals(1, m1.getEntryCount());
        assertEquals(wShort, m1.getTotalValueSize());

        cache.put("k", sLong); // replace
        TinyCacheMetrics m2 = cache.getMetrics();
        assertEquals(1, m2.getEntryCount());
        assertEquals(wLong, m2.getTotalValueSize());
    }
}
