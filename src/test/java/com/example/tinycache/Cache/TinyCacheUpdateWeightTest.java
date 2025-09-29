package com.example.tinycache.Cache;

import com.example.tinycache.TinyCache;
import com.example.tinycache.TinyCacheMetrics;
import com.example.tinycache.utils.Weigher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TinyCacheUpdateWeightTest {
    @Test
    void updatingExistingKeyReplacesWeight_usingByteWeigher() {
        Weigher weigher = new Weigher(); // uses JOL GraphLayout.totalSize() in BYTES
        TinyCache<String, String> cache = new TinyCache<>(100_000, weigher); // large cap to avoid eviction

        // Pick lengths that cross an 8-byte alignment bucket so sizes differ reliably
        String sShort = "A";                  // length 1
        String sLong  = "ABCDEFGHIJ";         // length 10

        int wShort = weigher.weigh(sShort);
        int wLong  = weigher.weigh(sLong);

        System.out.printf("[Weigher] '%s' deep size = %d bytes%n", sShort, wShort);
        System.out.printf("[Weigher] '%s' deep size = %d bytes%n", sLong,  wLong);

        assertTrue(wLong > wShort,
                "Expected longer string to weigh more (alignment buckets can mask small deltas).");

        // Put short value
        cache.put("k", sShort);
        TinyCacheMetrics m1 = cache.getMetrics();
        System.out.printf("[Cache] After put('%s'): entryCount=%d, totalValueSize=%d bytes%n",
                sShort, m1.getEntryCount(), m1.getTotalValueSize());

        assertEquals(1, m1.getEntryCount(), "Expected exactly one entry after first put");
        assertEquals(wShort, m1.getTotalValueSize(), "Total bytes should match weight of short value");

        // Replace with long value (weight must be replaced, not added)
        cache.put("k", sLong);
        TinyCacheMetrics m2 = cache.getMetrics();
        System.out.printf("[Cache] After put('%s'): entryCount=%d, totalValueSize=%d bytes%n",
                sLong, m2.getEntryCount(), m2.getTotalValueSize());

        assertEquals(1, m2.getEntryCount(), "Still one entry after replacement");
        assertEquals(wLong, m2.getTotalValueSize(), "Total bytes should match weight of long value");
    }
}
