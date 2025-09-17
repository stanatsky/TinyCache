package com.example.tinycache;

public class TinyCacheMetrics {
    private final long entryCount;
    private final long hits;
    private final long misses;
    private final long evictions;
    private final long totalValueSize;

    public TinyCacheMetrics(long entryCount,
                            long hits,
                            long misses,
                            long evictions,
                            long totalValueSize) {
        this.entryCount = entryCount;
        this.hits = hits;
        this.misses = misses;
        this.evictions = evictions;
        this.totalValueSize = totalValueSize;
    }

    public long getEntryCount() { return entryCount; }
    public long getHits() { return hits; }
    public long getMisses() { return misses; }
    public long getEvictions() { return evictions; }
    public long getTotalValueSize() { return totalValueSize; }
}
