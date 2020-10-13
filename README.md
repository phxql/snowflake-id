# SnowflakeId

Generates Twitter-like Snowflake ids.
In short, this is an id scheme to generate unique 64 bit ids which are roughly sortable across multiple systems without
a central instance. [See this blog post for more details](https://blog.twitter.com/engineering/en_us/a/2010/announcing-snowflake.html).

This project was heavily inspired by [IdGen](https://github.com/RobThree/IdGen) for C#, there's a great [How it works](https://github.com/RobThree/IdGen#how-it-works) in the readme, too.

The algorithm is implemented in plain Java without any dependencies. All you need is at least Java 11.

Such generated id in binary looks like this (this is 4425020822061056 in decimal):

```
0000000000001111101110001000100001110010001110010000000000000000
||                                            | |
|| Timestamp (16880114830)                    | | Sequence (0)
|                                             |
| Sign bit (always 0)                         | Generator id (1)
```

The structure used for this is 45 bits for the timestamp, 2 for the generator and the remaining 16 for the sequence.

It really uses only 63 bits of the 64 available to circumvent problems with unsigned longs. The generated values are always positive.

## How to use

### Simple example

```java
// generatorId must be unique over all your instances!
int generatorId = 0; 

// use default generator settings
SnowflakeIdGenerator generator = SnowflakeIdGenerator.createDefault(generatorId);

// generate 10 ids
for (int i = 0; i < 10; i++) {
    long id = generator.next();
    System.out.println(id);
}
```

### More configuration options

```java
// Use a custom epoch
TimeSource timeSource = new MonotonicTimeSource(Instant.parse("2020-04-01T00:00:00Z"));
// Use 45 bits for the timestamp, 2 bits for the generator and 16 bits for the sequence
Structure structure = new Structure(45, 2, 16);
// If the sequence number overflows, throw an exception
Options options = new Options(Options.SequenceOverflowStrategy.THROW_EXCEPTION);

// generatorId must be unique over all your instances!
long generatorId = 1;
SnowflakeIdGenerator generator = SnowflakeIdGenerator.createCustom(generatorId, timeSource, structure, options);

// generate 10 ids
for (int i = 0; i < 10; i++) {
    long id = generator.next();
    System.out.println(id);
}
```

## License

[LGPLv3](https://www.gnu.org/licenses/lgpl-3.0.html)