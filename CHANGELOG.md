# Change Log

All notable changes to this project will be documented in this file. This project adheres
to [Semantic Versioning](http://semver.org/).

## Next version - unreleased

- Improve JavaDoc
- Add new sleep overflow strategy

## [0.0.2] - 2023-01-15

- Add module descriptor. Module is named `de.mkammerer.snowflakeid`
- Switched `synchronized` to `ReentrantLock`
- Fixed bug where `SnowflakeIdGenerator` wasn't thread safe - [#4](https://github.com/phxql/snowflake-id/issues/4)

## [0.0.1] - 2020-10-18

- First official release
