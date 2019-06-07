# Clover Kafka high level spec


1  Kafka is used as a WR buffer for ClickHouse (CH)
2 InfluxDB is not buffered
3 TSP sink is always PostgreSQL
4 Platform supports batch-over-streaming processing model. Data frames are read from CH,  packed into Parquet frames and streamed to the Kafka Broker 
5 Platform should provide the following info on each update: broker address, topic to access, message size, number of messages in update (assuming all are the same length)
6 Each update will normally be a list of strings from the CH