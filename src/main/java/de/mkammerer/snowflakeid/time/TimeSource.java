package de.mkammerer.snowflakeid.time;

import java.time.Duration;
import java.time.Instant;

/**
 * Time source.
 *
 * @author Moritz Halbritter
 */
public interface TimeSource {
    /**
     * Returns the current tick.
     *
     * @return the current tick
     */
    long getTicks();

    /**
     * Returns the duration between ticks.
     *
     * @return the duration between ticks
     */
    Duration getTickDuration();

    /**
     * Returns the epoch. This is the instant from which this time source starts ticking.
     *
     * @return the epoch
     */
    Instant getEpoch();
}
