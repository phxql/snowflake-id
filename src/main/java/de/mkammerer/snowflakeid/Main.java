package de.mkammerer.snowflakeid;

import de.mkammerer.snowflakeid.options.Options;
import de.mkammerer.snowflakeid.structure.Structure;
import de.mkammerer.snowflakeid.time.MonotonicTimeSource;
import de.mkammerer.snowflakeid.time.TimeSource;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class Main {
    // TODO: Remove, maybe move to a test
    public static void main(String[] args) {
        TimeSource timeSource = new MonotonicTimeSource(Instant.parse("2020-04-01T00:00:00Z"));

        Structure structure = new Structure(45, 2, 16);
        System.out.println("Max generators: " + structure.maxGenerators());
        System.out.println("Max sequences per ms per generator: " + structure.maxSequenceIds());
        System.out.println("Max sequences per ms total: " + structure.maxSequenceIds() * structure.maxGenerators());
        System.out.println("Wraparound duration: " + structure.calculateWraparoundDuration(timeSource));
        System.out.println("Wraparound date: " + structure.calculateWraparoundDate(timeSource));

        Options options = Options.createDefault();

        SnowflakeIdGenerator gen0 = SnowflakeIdGenerator.createCustom(0, timeSource, structure, options);
        SnowflakeIdGenerator gen1 = SnowflakeIdGenerator.createCustom(1, timeSource, structure, options);
        SnowflakeIdGenerator gen2 = SnowflakeIdGenerator.createCustom(2, timeSource, structure, options);
        SnowflakeIdGenerator gen3 = SnowflakeIdGenerator.createCustom(3, timeSource, structure, options);

        Set<Long> set = new HashSet<>();

        for (int i = 0; i < 100_000; i++) {
            addToSet(set, gen0.next());
            addToSet(set, gen1.next());
            addToSet(set, gen2.next());
            addToSet(set, gen3.next());
        }

        // Print first id
        System.out.println(set.iterator().next());
    }

    private static void addToSet(Set<Long> set, long id) {
        // Check for uniqueness
        if (!set.add(id)) {
            throw new IllegalStateException(id + " is a duplicate");
        }
    }
}
