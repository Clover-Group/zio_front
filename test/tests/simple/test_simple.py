from pathlib import Path

from tests import utils
from tests.base import BaseTestCase

"""

This unit test helps to check base functionality of zio_front.

Designed by Andrey Romanov
Clover Group. 03/08/2019

"""


class SimpleFrontTestCase(BaseTestCase):
    """
    Simple Test Case for zio_front.
    """

    def setUp(self):

        super().setUp()

        self.server_url = 'http://{0}:{1}'.format(
            self.config['test_server']['host'],
            self.config['test_server']['port']
        )

        self.kafka_file_path = Path(
            __file__
        ).parent.parent.parent.parent / Path('assets/json/loco/kafka.json')

    def test_kafka_processing(self):
        """Method with Kafka upload data."""

        self.expected_data = {
            'data': 'some data'
        }

        server_path = '{0}/tsp_processing'.format(self.server_url)

        actual_data = utils.send_request(server_path, self.kafka_file_path)

        self.assertEqual(self.expected_data, actual_data, 'Полученные данные не совпадают')


