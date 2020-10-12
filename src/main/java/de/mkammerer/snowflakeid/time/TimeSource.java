package de.mkammerer.snowflakeid.time;

import java.time.Duration;
import java.time.Instant;

public interface TimeSource {
    long getTicks();

    Duration getTickDuration();

    Instant getEpoch();
}
