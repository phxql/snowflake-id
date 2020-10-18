package de.mkammerer.snowflakeid.test;

import de.mkammerer.snowflakeid.time.TimeSource;

import java.time.Duration;
import java.time.Instant;

public class MockTimeSource implements TimeSource {
    public static final Instant DEFAULT_EPOCH = Instant.parse("2020-01-01T00:00:00Z");

    private final Instant epoch;
    private long ticks;

    public MockTimeSource(Instant epoch, long ticks) {
        this.epoch = epoch;
        this.ticks = ticks;
    }

    public void setTicks(long ticks) {
        this.ticks = ticks;
    }

    @Override
    public long getTicks() {
        return ticks;
    }

    @Override
    public Duration getTickDuration() {
        return Duration.ofMillis(1);
    }

    @Override
    public Instant getEpoch() {
        return epoch;
    }
}
