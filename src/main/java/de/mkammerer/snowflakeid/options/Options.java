package de.mkammerer.snowflakeid.options;

import java.util.Objects;

public class Options {
    private final SequenceOverflowStrategy sequenceOverflowStrategy;

    public Options(SequenceOverflowStrategy sequenceOverflowStrategy) {
        this.sequenceOverflowStrategy = Objects.requireNonNull(sequenceOverflowStrategy, "sequenceOverflowStrategy");
    }

    public SequenceOverflowStrategy getSequenceOverflowStrategy() {
        return sequenceOverflowStrategy;
    }

    /**
     * Creates options with default settings.
     * <p>
     * If a sequence overflow occurs, uses spin wait to wait for the next timestamp.
     *
     * @return options
     */
    public static Options createDefault() {
        return new Options(SequenceOverflowStrategy.SPIN_WAIT);
    }

    @Override
    public String toString() {
        return "Options{" +
            "sequenceOverflowStrategy=" + sequenceOverflowStrategy +
            '}';
    }

    public enum SequenceOverflowStrategy {
        THROW_EXCEPTION,
        SPIN_WAIT
    }
}
