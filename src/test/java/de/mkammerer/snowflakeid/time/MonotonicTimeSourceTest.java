package de.mkammerer.snowflakeid.time;

import de.mkammerer.snowflakeid.test.MockTimeSource;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MonotonicTimeSourceTest {
    private MonotonicTimeSource sut;

    @BeforeEach
    void setUp() {
        sut = new MonotonicTimeSource(MockTimeSource.DEFAULT_EPOCH);
    }

    @Test
    void increasing() {
        long start = sut.getTicks();
        Awaitility.await().atLeast(Duration.ofMillis(1));
        long next = sut.getTicks();

        assertThat(next).isGreaterThan(start);
    }
}