package de.mkammerer.snowflakeid;

import de.mkammerer.snowflakeid.options.Options;
import de.mkammerer.snowflakeid.structure.Structure;
import de.mkammerer.snowflakeid.test.MockTimeSource;
import de.mkammerer.snowflakeid.time.MonotonicTimeSource;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SnowflakeIdGeneratorTest {
    @Test
    void generate_unique() {
        // Use two generators to generate 20000 ids and check them for uniqueness and increasing order

        SnowflakeIdGenerator generator0 = SnowflakeIdGenerator.createDefault(0);
        SnowflakeIdGenerator generator1 = SnowflakeIdGenerator.createDefault(1);
        int count = 10_000;

        Set<Long> ids = new HashSet<>(count);

        long lastGen0Id = -1;
        long lastGen1Id = -1;

        for (int i = 0; i < count; i++) {
            long gen0id = generator0.next();
            long gen1id = generator1.next();

            // All ids from the same generator are increasing
            assertThat(gen0id).isGreaterThan(lastGen0Id);
            lastGen0Id = gen0id;
            assertThat(gen1id).isGreaterThan(lastGen1Id);
            lastGen1Id = gen1id;

            if (!ids.add(gen0id)) {
                fail(gen0id + " is a duplicate");
            }
            if (!ids.add(gen1id)) {
                fail(gen1id + " is a duplicate");
            }
        }

        assertThat(ids).hasSize(count * 2);
    }

    @Test
    void forbid_negative_generator_id() {
        assertThatThrownBy(() ->
            SnowflakeIdGenerator.createDefault(-1)
        ).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("generatorId");
    }

    @Test
    void forbid_too_big_generator_id() {
        assertThatThrownBy(() ->
            // Maximum generator id is 7
            SnowflakeIdGenerator.createCustom(1024, MonotonicTimeSource.createDefault(), new Structure(50, 3, 10), Options.createDefault())
        ).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("generatorId");
    }

    @Test
    void sequence_overflow_exception() {
        // We use 1 bit for the sequence, this should overflow pretty fast!
        SnowflakeIdGenerator sut = SnowflakeIdGenerator.createCustom(0, MonotonicTimeSource.createDefault(), new Structure(50, 12, 1), new Options(Options.SequenceOverflowStrategy.THROW_EXCEPTION));

        assertThatThrownBy(() -> {
            for (int i = 0; i < 10000; i++) {
                sut.next();
            }
        }).isInstanceOf(IllegalStateException.class).hasMessageContaining("Sequence overflow");
    }

    @Test
    void sequence_overflow_spin_wait() {
        // We use 1 bit for the sequence, this should overflow pretty fast!
        SnowflakeIdGenerator sut = SnowflakeIdGenerator.createCustom(0, MonotonicTimeSource.createDefault(), new Structure(50, 12, 1), new Options(Options.SequenceOverflowStrategy.SPIN_WAIT));

        assertThatCode(() -> {
            for (int i = 0; i < 10; i++) {
                sut.next();
            }
        }).doesNotThrowAnyException();
    }

    @Test
    void protect_against_negative_ticks() {
        MockTimeSource mockTimeSource = new MockTimeSource(MockTimeSource.DEFAULT_EPOCH, -1);

        SnowflakeIdGenerator sut = SnowflakeIdGenerator.createCustom(0, mockTimeSource, Structure.createDefault(), Options.createDefault());

        assertThatThrownBy(() ->
            sut.next()
        ).isInstanceOf(IllegalStateException.class).hasMessageContaining("negative ticks");
    }

    @Test
    void protect_against_clock_moved_backwards() {
        MockTimeSource mockTimeSource = new MockTimeSource(MockTimeSource.DEFAULT_EPOCH, 1);

        SnowflakeIdGenerator sut = SnowflakeIdGenerator.createCustom(0, mockTimeSource, Structure.createDefault(), Options.createDefault());

        mockTimeSource.setTicks(2);
        sut.next();

        mockTimeSource.setTicks(1);
        assertThatThrownBy(() ->
            sut.next()
        ).isInstanceOf(IllegalStateException.class).hasMessageContaining("moved backwards");

    }
}