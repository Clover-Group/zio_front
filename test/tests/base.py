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

        self.server_url = 'http://{0}:{1}'.format(
            self.config['test_server']['host'],
            self.config['test_server']['port']
        )

        self.server_path = '{0}/tsp_processing'.format(self.server_url)


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


