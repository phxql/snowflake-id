package de.mkammerer.snowflakeid.time;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class MonotonicTimeSource implements TimeSource {
    private final long start;
    private final long offset;
    private final Instant epoch;

    public MonotonicTimeSource(Instant epoch) {
        this.epoch = Objects.requireNonNull(epoch, "epoch");

        // Offset elapsed time by this amount (creation time of this time source since epoch)
        offset = Instant.now().toEpochMilli() - epoch.toEpochMilli();
        // Record creation of this time source in milliseconds
        start = System.nanoTime() / 1_000_000;
    }

    @Override
    public long getTicks() {
        return offset + elapsed();
    }

    @Override
    public Duration getTickDuration() {
        return Duration.ofMillis(1);
    }

    @Override
    public Instant getEpoch() {
        return epoch;
    }

    /**
     * Creates a time source with default settings.
     * <p>
     * Uses 2020-01-01T00:00:00Z as epoch.
     *
     * @return time source
     */
    public static MonotonicTimeSource createDefault() {
        // 2020-01-01T00:00:00Z
        return new MonotonicTimeSource(Instant.ofEpochMilli(1577836800000L));
    }

    private long elapsed() {
        // Calculate elapsed time since creation of this time source in milliseconds
        return (System.nanoTime() / 1_000_000) - start;
    }

    @Override
    public String toString() {
        return "MonotonicTimeSource{" +
            "epoch=" + epoch +
            '}';
    }
}
