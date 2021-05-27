package de.mkammerer.snowflakeid.time;

import de.mkammerer.snowflakeid.test.MockTimeSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MonotonicTimeSourceTest {
    private MonotonicTimeSource sut;

    @BeforeEach
    public void setUp() {
        sut = new MonotonicTimeSource(MockTimeSource.DEFAULT_EPOCH);
    }

    @Test
    public void increasing() throws InterruptedException {
        long start = sut.getTicks();
        Thread.sleep(10);
        long next = sut.getTicks();

        assertThat(next).isGreaterThan(start);
    }
}