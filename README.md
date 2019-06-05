# zio_front
ZIO frontend for Clover Group AI platform. This features ZIO, http4s, doobie, fs2 and kafka. Originally taken from [here](https://github.com/mschuwalow/zio-todo-backend)

## High level datapath descrition
This project integrates REST frontend thru [http4s](https://http4s.org/) and several data sources. It supports [PostgreSQL](https://www.postgresql.org/), [ClickHouse](https://clickhouse.yandex/) and [InfluxDB](https://www.influxdata.com/), supported thru native drivers, and [Doobie](https://tpolecat.github.io/doobie/), which unifies JDBC interfaces and supports pure functional effects. 

[Kafka](https://kafka.apache.org/) is used to build streaming connections between server nodes and serialize large data arrays.
[Parquet](https://parquet.apache.org/) is used for efficient data source management and is streamed thrue Kafka.

The application interface to business logic is implemented with [ZIO queue](https://zio.dev/docs/datatypes/datatypes_queue). Business logic hevily relies on [ZIO Fibers](https://zio.dev/docs/datatypes/datatypes_fiber) for massive server side multithreading.

This project is a high quality design, built upon many successful projects. It became an official member of [ZIO Ecosystem](https://zio.dev/docs/resources/resources)

Project datapath block diagram
