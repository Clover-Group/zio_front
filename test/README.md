# Python Tests For zio_front

## Setup

```
pip3 install virtualenv
python3 -m virtualenv zio_front_env
source zio_front_env/bin/activate
pip install poetry
poetry install
```

## Launch tests

#### Before launch, please, start the main project

```
sbt
compile
run clover.http.front.Main
```

#### For example, launch of integration test:
```
python -m unittest tests.integration.test_clickhouse.ClickHouseTestCase
```