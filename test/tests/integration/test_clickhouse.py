from pathlib import Path

from tests import utils
from tests.base import BaseIntegrationTestCase

"""

This unit test helps to check processing of ClickHouse JSON file

Designed by Andrey Romanov
Clover Group. 09/08/2019

"""


class ClickHouseTestCase(BaseIntegrationTestCase):

    def setUp(self):
        super().setUp()

        self.server_url = 'http://{0}:{1}'.format(
            self.config['test_server']['host'],
            self.config['test_server']['port']
        )

        self.ch_file_path = Path(
            __file__
        ).parent.parent.parent.parent / Path('assets/json/req0.txt')

    def test_ch_processing(self):
        """Method with ClickHouse upload data."""

        self.expected_data = {
            'data': 'some data'
        }

        server_path = '{0}/tsp_processing'.format(self.server_url)

        actual_data = utils.send_request(server_path, self.ch_file_path)

        self.assertEqual(self.expected_data, actual_data, 'Полученные данные не совпадают')
