import unittest
import time
from pathlib import Path

import lya
from testcontainers.compose import DockerCompose


class BaseTestCase(unittest.TestCase):
    """
    Base test case for all tests.
    """

    def setUp(self):
        config_path = Path(__file__).parent.parent / Path('config.yml')
        self.config = lya.AttrDict.from_yaml(str(config_path))


class BaseIntegrationTestCase(BaseTestCase):
    """
    Base test case for all integration tests.
    """

    containers: DockerCompose

    @classmethod
    def setUpClass(cls):
        super().setUpClass()

        cls.containers = DockerCompose('tests/fixtures/config')
        cls.containers.start()

        time.sleep(10)

    @classmethod
    def tearDownClass(cls):
        super().tearDownClass()
        cls.containers.stop()


