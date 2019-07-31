# TSP Status Report

```mermaid
graph TD;  

  %% Top level
  TSP --> Front
  TSP --> Core
  TSP --> DSL
  TSP --> Integration
  TSP --> Parallelism
  TSP --> QA
  
  %% Datapath
  Front --> Kafka(Kafka<br>Consumer)
  Kafka --> Arrow(Arrow<br>Decoder)
  Kafka --> Parquet(Parquet<br>Decoder)  

  %% HTTP
  Front --> http(HTTP<br>service)

  %% JDBC
  Front --> JDBC(JDBC<br> ZIO)
  JDBC --> Postgre(Postgre<br>Doobie)
  JDBC --> WrapInflux(InfluxDB<br>ZIO)
  JDBC --> WrapCH(Clickhouse<br>ZIO)
  WrapInflux --> Influx(InfluxDB<br>Native)
  WrapCH --> ClickHouse(ClichkHouse<br>Native)

  %% Core
  Core --> CoreWrap(Core<br> Top)
  Core --> ReqOpt(Request<br> Optimizer)

  %% DSL
  DSL --> DslTop(DSL<br>Top);
  
  %% Styling

  %% Ready
  style Kafka fill:#9f9
  
  %% Grads 
  style Postgre fill:dodgerblue
  style http fill:dodgerblue

  %% Alfa Stage
  style TSP fill:bisque
  style DSL fill:bisque  
  style Integration fill:bisque
  style ReqOpt fill:bisque
  style Parquet fill:bisque
  
  %% Beta Stage
  style Arrow fill:gold
  style Front fill:gold
  style JDBC fill:gold
  style Core fill:gold
  
  %% Release Candidate

  %% Reused
  style CoreWrap fill:magenta
  style DslTop fill:magenta
  
```
## Color Encoding
Blank     - Not Started, Not implemented <br>
Green     - Done <br>
Blue      - Graduates Work <br>
Bisque    - Alfa <br>
Gold      - Beta <br>
Orange    - Release Candidate <br>
Magenta   - Reused

