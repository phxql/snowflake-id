package de.mkammerer.snowflakeid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SnowflakeIdGeneratorTest {
    private SnowflakeIdGenerator sut;

    @BeforeEach
    void setUp() {
        sut = SnowflakeIdGenerator.createDefault(0);
    }

    @Test
    void generate_unique() {
        int count = 10000;

        Set<Long> ids = new HashSet<>(count);

        for (int i = 0; i < count; i++) {
            long id = sut.next();

            if (!ids.add(id)) {
                fail(id + " is a duplicate");
            }
        }

        assertThat(ids).hasSize(count);
    }
}