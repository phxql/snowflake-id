package de.mkammerer.snowflakeid.structure;

import de.mkammerer.snowflakeid.test.MockTimeSource;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class StructureTest {
    @Test
    public void timestamp_bits_cant_be_zero() {
        assertThatThrownBy(() ->
            new Structure(0, 31, 31)
        ).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("timestampBits");
    }

    @Test
    public void generator_bits_cant_be_zero() {
        assertThatThrownBy(() ->
            new Structure(31, 0, 31)
        ).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("generatorBits");
    }

    @Test
    public void generator_bits_cant_more_than_31() {
        assertThatThrownBy(() ->
            new Structure(31, 32, 0)
        ).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("generatorBits");
    }

    @Test
    public void sequence_bits_cant_be_zero() {
        assertThatThrownBy(() ->
            new Structure(31, 31, 0)
        ).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("sequenceBits");
    }

    @Test
    public void sequence_bits_cant_more_than_31() {
        assertThatThrownBy(() ->
            new Structure(31, 1, 32)
        ).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("sequenceBits");
    }

    @Test
    public void parts_must_sum_to_63() {
        assertThatThrownBy(() ->
            new Structure(1, 1, 1)
        ).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("must be 63");
    }

    @Test
    public void max_timestamps() {
        Structure structure = new Structure(31, 10, 22);

        assertThat(structure.maxTimestamps()).isEqualTo(2147483648L);
    }

    @Test
    public void max_generators() {
        Structure structure = new Structure(31, 10, 22);

        assertThat(structure.maxGenerators()).isEqualTo(1024L);
    }

    @Test
    public void max_sequence_ids() {
        Structure structure = new Structure(31, 10, 22);

        assertThat(structure.maxSequenceIds()).isEqualTo(4194304L);
    }

    @Test
    public void calculate_wraparound_duration() {
        Structure structure = new Structure(31, 10, 22);

        MockTimeSource mockTimeSource = new MockTimeSource(MockTimeSource.DEFAULT_EPOCH, 0);

        assertThat(structure.calculateWraparoundDuration(mockTimeSource)).isEqualTo(Duration.ofMillis(2147483648L));
    }

    @Test
    public void calculate_wraparound_date() {
        Structure structure = new Structure(31, 10, 22);

        MockTimeSource mockTimeSource = new MockTimeSource(Instant.parse("2020-01-01T00:00:00Z"), 0);

        // epoch + 2147483648 milliseconds
        assertThat(structure.calculateWraparoundDate(mockTimeSource)).isEqualTo(Instant.parse("2020-01-25T20:31:23.648Z"));
    }
}