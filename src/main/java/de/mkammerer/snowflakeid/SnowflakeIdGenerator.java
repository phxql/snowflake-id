package de.mkammerer.snowflakeid;

import de.mkammerer.snowflakeid.options.Options;
import de.mkammerer.snowflakeid.structure.Structure;
import de.mkammerer.snowflakeid.time.MonotonicTimeSource;
import de.mkammerer.snowflakeid.time.TimeSource;

import java.util.Objects;

public class SnowflakeIdGenerator {
    /**
     * Lock for sequence and lastTimestamp.
     */
    private final Object lock = new Object();

    // Stuff which is set in the constructor
    private final long generatorId;
    private final TimeSource timeSource;
    private final Structure structure;
    private final Options options;

    // precalculated variables for bit magic
    private final long maskSequence;
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
    private int sequence = 0;

    // Structure:
    // time || generator || sequence
    private SnowflakeIdGenerator(long generatorId, TimeSource timeSource, Structure structure, Options options) {
        this.timeSource = Objects.requireNonNull(timeSource, "timeSource");
        this.structure = Objects.requireNonNull(structure, "structure");
        this.options = Objects.requireNonNull(options, "options");

        int maxGeneratorId = 1 << structure.getGeneratorBits();
        if (generatorId < 0 || generatorId >= maxGeneratorId) {
            throw new IllegalArgumentException("generatorId must be between 0 (inclusive) and " + maxGeneratorId + " (exclusive), but was " + generatorId);
        }

        this.generatorId = generatorId;

        maskTime = calculateMask(structure.getTimestampBits());
        maskSequence = calculateMask(structure.getSequenceBits());
        shiftTime = structure.getGeneratorBits() + structure.getSequenceBits();
        shiftGenerator = structure.getSequenceBits();
    }

    /**
     * Generates the next id.
     *
     * @return next id
     * @throws IllegalStateException if some invariant has been broken, e.g. the clock moved backwards or a sequence overflow occured
     */
    public long next() {
        long ticks = timeSource.getTicks();
        if (ticks < 0) {
            throw new IllegalStateException("Clock gave negative ticks");
        }
        long timestamp = ticks & maskTime;

        synchronized (lock) {
            // Guard against non-monotonic clocks
            if (timestamp < lastTimestamp) {
                throw new IllegalStateException("Timestamp moved backwards or wrapped around");
            }

            if (timestamp == lastTimestamp) {
                // Same timeslot
                if (sequence >= maskSequence) {
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
        }
    }

    public long getGeneratorId() {
        return generatorId;
    }

    public TimeSource getTimeSource() {
        return timeSource;
    }

    public Options getOptions() {
        return options;
    }

    public Structure getStructure() {
        return structure;
    }

    public static SnowflakeIdGenerator createCustom(long generatorId, TimeSource timeSource, Structure structure, Options options) {
        return new SnowflakeIdGenerator(generatorId, timeSource, structure, options);
    }

    /**
     * Creates a generator with default settings.
     * <p>
     * Uses 2020-01-01T00:00:00Z as epoch, 41 bits for the timestamp, 10 for the generator id and 12 for the sequence. If a
     * sequence overflow occurs, uses spin wait to wait for the next timestamp.
     *
     * @param generatorId id of the generator. Must be unique across all instances
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
            default:
                throw new AssertionError("Unexpected enum value: " + this.options.getSequenceOverflowStrategy());
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
