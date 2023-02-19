package de.mkammerer.snowflakeid.options;

import java.util.Objects;

/**
 * Options.
 *
 * @author Moritz Halbritter
 */
public class Options {
    private final SequenceOverflowStrategy sequenceOverflowStrategy;

    /**
     * Constructor.
     *
     * @param sequenceOverflowStrategy the sequence overflow strategy
     */
    public Options(SequenceOverflowStrategy sequenceOverflowStrategy) {
        this.sequenceOverflowStrategy = Objects.requireNonNull(sequenceOverflowStrategy, "sequenceOverflowStrategy");
    }

    /**
     * Returns the sequence overflow strategy.
     *
     * @return the sequence overflow strategy
     */
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

    /**
     * Sequence overflow strategy.
     */
    public enum SequenceOverflowStrategy {
        /**
         * Throws an exception if a sequence overflow occurs.
         */
        THROW_EXCEPTION,
        /**
         * Spin waits for the next sequence if a sequence overflow occurs.
         */
        SPIN_WAIT,
        /**
         * Sleeps until the next sequence if a sequence overflow occurs.
         */
        SLEEP
    }
}
