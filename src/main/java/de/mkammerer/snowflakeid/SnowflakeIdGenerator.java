package de.mkammerer.snowflakeid;

import de.mkammerer.snowflakeid.options.Options;
import de.mkammerer.snowflakeid.structure.Structure;
import de.mkammerer.snowflakeid.time.MonotonicTimeSource;
import de.mkammerer.snowflakeid.time.TimeSource;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Generates snowflake ids. This class is thread safe.
 */
public class SnowflakeIdGenerator {
    /**
     * Lock for sequence and lastTimestamp.
     */
    private final Lock lock = new ReentrantLock();

    // Stuff which is set in the constructor
    private final long generatorId;
    private final TimeSource timeSource;
    private final Structure structure;
    private final Options options;

    // precalculated variables for bit magic
    private final long maxSequence;
    private final long maskTime;
    private final int shiftTime;
    private final int shiftGenerator;

    /**
     * Tracks the last generated timestamp.
     */
    private long lastTimestamp = -1;
    /**
     * Sequence number, unique per timestamp.
     */
    private long sequence = 0;

    // Structure:
    // time || generator || sequence
    private SnowflakeIdGenerator(long generatorId, TimeSource timeSource, Structure structure, Options options) {
        this.timeSource = Objects.requireNonNull(timeSource, "timeSource");
        this.structure = Objects.requireNonNull(structure, "structure");
        this.options = Objects.requireNonNull(options, "options");

        if (generatorId < 0 || generatorId >= structure.maxGenerators()) {
            throw new IllegalArgumentException("generatorId must be between 0 (inclusive) and " + structure.maxGenerators() + " (exclusive), but was " + generatorId);
        }

        this.generatorId = generatorId;

        maskTime = calculateMask(structure.getTimestampBits());
        maxSequence = calculateMask(structure.getSequenceBits());
        shiftTime = structure.getGeneratorBits() + structure.getSequenceBits();
        shiftGenerator = structure.getSequenceBits();
    }

    /**
     * Generates the next id.
     *
     * @return next id
     * @throws IllegalStateException if some invariant has been broken, e.g. the clock moved backwards or a sequence overflow occurred
     */
    public long next() {
        lock.lock();
        try {
            long ticks = timeSource.getTicks();
            if (ticks < 0) {
                throw new IllegalStateException("Clock gave negative ticks");
            }
            long timestamp = ticks & maskTime;

            // Guard against non-monotonic clocks
            if (timestamp < lastTimestamp) {
                throw new IllegalStateException("Timestamp moved backwards or wrapped around");
            }

            if (timestamp == lastTimestamp) {
                // Same timeslot
                if (sequence >= maxSequence) {
                    handleSequenceOverflow();
                    return next();
                }
                sequence++;
            } else {
                // other timeslot, reset sequence
                sequence = 0;
                lastTimestamp = timestamp;
            }

            return (timestamp << shiftTime) + (generatorId << shiftGenerator) + sequence;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the generator id.
     *
     * @return the generator id
     */
    public long getGeneratorId() {
        return generatorId;
    }

    /**
     * Returns the time source.
     *
     * @return the time source
     */
    public TimeSource getTimeSource() {
        return timeSource;
    }

    /**
     * Returns the options.
     *
     * @return the options
     */
    public Options getOptions() {
        return options;
    }

    /**
     * Returns the structure.
     *
     * @return the structure
     */
    public Structure getStructure() {
        return structure;
    }

    /**
     * Creates a custom snowflake id generator.
     *
     * @param generatorId the id of the generator. Must be unique across all instances
     * @param timeSource  the timesource to use
     * @param structure   the id structure
     * @param options     the options
     * @return the created snowflake id generator
     */
    public static SnowflakeIdGenerator createCustom(long generatorId, TimeSource timeSource, Structure structure, Options options) {
        return new SnowflakeIdGenerator(generatorId, timeSource, structure, options);
    }

    /**
     * Creates a generator with default settings.
     * <p>
     * Uses 2020-01-01T00:00:00Z as epoch, 41 bits for the timestamp, 10 for the generator id and 12 for the sequence. If a
     * sequence overflow occurs, uses spin wait to wait for the next timestamp.
     *
     * @param generatorId the id of the generator. Must be unique across all instances
     * @return generator
     */
    public static SnowflakeIdGenerator createDefault(int generatorId) {
        return new SnowflakeIdGenerator(generatorId, MonotonicTimeSource.createDefault(), Structure.createDefault(), Options.createDefault());
    }

    private void handleSequenceOverflow() {
        switch (this.options.getSequenceOverflowStrategy()) {
            case THROW_EXCEPTION:
                throw new IllegalStateException("Sequence overflow");
            case SPIN_WAIT:
                spinWaitForNextTick(lastTimestamp);
                break;
            case SLEEP:
                sleepForTickDuration();
                break;
            default:
                throw new AssertionError("Unexpected enum value: " + this.options.getSequenceOverflowStrategy());
        }
    }

    private void sleepForTickDuration() {
        try {
            Thread.sleep(timeSource.getTickDuration().toMillis());
        } catch (InterruptedException e) {
            // Do nothing
        }
    }

    private void spinWaitForNextTick(long lastTimestamp) {
        long timestamp;
        do {
            Thread.onSpinWait();
            timestamp = timeSource.getTicks() & maskTime;
        } while (timestamp == lastTimestamp);
    }

    private long calculateMask(int bits) {
        return (1L << bits) - 1;
    }

    @Override
    public String toString() {
        return "SnowflakeIdGenerator{" +
            "generatorId=" + generatorId +
            ", timeSource=" + timeSource +
            ", structure=" + structure +
            ", options=" + options +
            '}';
    }
}
