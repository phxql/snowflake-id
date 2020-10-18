package de.mkammerer.snowflakeid.time;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MonotonicTimeSourceTest {
    private MonotonicTimeSource sut;

    @BeforeEach
    void setUp() {
        sut = new MonotonicTimeSource(Instant.parse("2020-01-01T00:00:00Z"));
    }

    @Test
    void increasing() {
        long start = sut.getTicks();
        Awaitility.await().atLeast(Duration.ofMillis(1));
        long next = sut.getTicks();

        assertThat(next).isGreaterThan(start);
    }
}