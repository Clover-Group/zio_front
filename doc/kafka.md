# Clover Kafka high level spec

## General requirements 

1. Kafka is used as a WR buffer for ClickHouse (CH)
2. Direct CH support should be added to support legacy code (lower priority feature)
2. InfluxDB is not buffered
3. TSP sink is always PostgreSQL
4. Platform supports batch-over-streaming processing model. Data frames are read from CH,  packed into Parquet frames and streamed to the Kafka Broker 
5. Platform should provide the following info on each update: broker address, topic to access, message size, number of messages in update (assuming all are the same length)
6. Each update will normally be a list of strings from the CH

## Data format

1. Kafka is used as a high level persistent streaming engine
2. It carries Parquet frames with business info
3. Parquet frames also encapsulate a schema for underlying data frames
4. Each data frame is variable in size and may be produced from different nodes. It's a Producer (Platform) responsibility to add all meta info to Parquet frames
5. Parquet frames encapsulate Python's Pandas frames
6. TSP should extract data from Parquet and Pandas
7. TSP should parse Pandas Frames


