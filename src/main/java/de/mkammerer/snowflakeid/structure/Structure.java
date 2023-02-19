package de.mkammerer.snowflakeid.structure;

import de.mkammerer.snowflakeid.time.TimeSource;

import java.time.Duration;
import java.time.Instant;

/**
 * Id structure.
 */
public class Structure {
    private final int timestampBits;
    private final int generatorBits;
    private final int sequenceBits;

    /**
     * Constructor.
     *
     * @param timestampBits the bits used for the timestamp. Must be greater than 0
     * @param generatorBits the bits used for the generator. Must be between 1 (inclusive) and 31 (inclusive)
     * @param sequenceBits  the bits used for the sequence. Must be between 1 (inclusive) and 31 (inclusive)
     * @throws IllegalArgumentException if an argument is invalid, or if the bits don't add up to 63
     */
    public Structure(int timestampBits, int generatorBits, int sequenceBits) {
        if (timestampBits < 1) {
            throw new IllegalArgumentException("timestampBits must no be <= 0, but was " + timestampBits);
        }
        if (generatorBits < 1 || generatorBits > 31) {
            throw new IllegalArgumentException("generatorBits must be between 1 (inclusive) and 31 (inclusive), but was " + generatorBits);
        }
        if (sequenceBits < 1 || sequenceBits > 31) {
            throw new IllegalArgumentException("sequenceBits must be between 1 (inclusive) and 31 (inclusive), but was " + sequenceBits);
        }

        int sum = timestampBits + generatorBits + sequenceBits;
        if (sum != 63) {
            throw new IllegalArgumentException("timestampBits + generatorBits + sequenceBits must be 63, but was " + sum);
        }

        this.timestampBits = timestampBits;
        this.generatorBits = generatorBits;
        this.sequenceBits = sequenceBits;
    }

    /**
     * Returns the bits used for the timestamp.
     *
     * @return the bits used for the timestamp
     */
    public int getTimestampBits() {
        return timestampBits;
    }

    /**
     * Returns the bits used for the generator.
     *
     * @return the bits used for the generator
     */
    public int getGeneratorBits() {
        return generatorBits;
    }

    /**
     * Returns the bits used for the sequence.
     *
     * @return the bits used for the sequence
     */
    public int getSequenceBits() {
        return sequenceBits;
    }

    /**
     * Returns the maximum number of generators.
     *
     * @return the maximum number of generators
     */
    public long maxGenerators() {
        return 1L << generatorBits;
    }

    /**
     * Returns the maximum number of sequence ids.
     *
     * @return the maximum number of sequence ids
     */
    public long maxSequenceIds() {
        return 1L << sequenceBits;
    }

    /**
     * Returns the maximum number of timestamps.
     *
     * @return the maximum number of timestamps
     */
    public long maxTimestamps() {
        return 1L << timestampBits;
    }

    /**
     * Calculates when the sequence ids will wrap around
     *
     * @param timeSource the used time source
     * @return the wrap around duration
     */
    public Duration calculateWraparoundDuration(TimeSource timeSource) {
        return timeSource.getTickDuration().multipliedBy(maxTimestamps());
    }

    /**
     * Calculates when the sequence ids will wrap around
     *
     * @param timeSource the used time source
     * @return the wrap around instant
     */
    public Instant calculateWraparoundDate(TimeSource timeSource) {
        return timeSource.getEpoch().plus(calculateWraparoundDuration(timeSource));
    }

    /**
     * Creates a structure with default settings.
     * <p>
     * Uses 41 bits for the timestamp, 10 for the generator id and 12 for the sequence.
     *
     * @return structure
     */
    public static Structure createDefault() {
        return new Structure(41, 10, 12);
    }

    @Override
    public String toString() {
        return "Structure{" +
            "timestampBits=" + timestampBits +
            ", generatorBits=" + generatorBits +
            ", sequenceBits=" + sequenceBits +
            '}';
    }
}
