package de.mkammerer.snowflakeid.structure;

import de.mkammerer.snowflakeid.time.TimeSource;

import java.time.Duration;
import java.time.Instant;

public class Structure {
    private final int timestampBits;
    private final int generatorBits;
    private final int sequenceBits;

    public Structure(int timestampBits, int generatorBits, int sequenceBits) {
        if (timestampBits < 0) {
            throw new IllegalArgumentException("timestampBits must no be < 0, but was " + timestampBits);
        }
        if (generatorBits < 0) {
            throw new IllegalArgumentException("generatorBits must no be < 0, but was " + generatorBits);
        }
        if (sequenceBits < 0) {
            throw new IllegalArgumentException("sequenceBits must no be < 0, but was " + sequenceBits);
        }

        int sum = timestampBits + generatorBits + sequenceBits;
        if (sum != 63) {
            throw new IllegalArgumentException("timestampBits + generatorBits + sequenceBits must be 63, but was " + sum);
        }
        if (generatorBits > 31) {
            throw new IllegalArgumentException("generatorBits must not be > 31, but was " + generatorBits);
        }
        if (sequenceBits > 31) {
            throw new IllegalArgumentException("sequenceBits must not be > 31, but was " + sequenceBits);
        }

        this.timestampBits = timestampBits;
        this.generatorBits = generatorBits;
        this.sequenceBits = sequenceBits;
    }

    public int getTimestampBits() {
        return timestampBits;
    }

    public int getGeneratorBits() {
        return generatorBits;
    }

    public long maxGenerators() {
        return 1L << generatorBits;
    }

    public int getSequenceBits() {
        return sequenceBits;
    }

    public long maxSequenceIds() {
        return 1L << sequenceBits;
    }

    public long maxTimestamps() {
        return 1L << timestampBits;
    }

    public Duration calculateWraparoundDuration(TimeSource timeSource) {
        return timeSource.getTickDuration().multipliedBy(maxTimestamps());
    }

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
