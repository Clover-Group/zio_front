from pathlib import Path

from tests import utils
from tests.base import BaseIntegrationTestCase

"""

This unit test helps to check processing of JSON files to TSP

Designed by Andrey Romanov
Clover Group. 11/08/2019

"""


class SimpleIntegrationTestCase(BaseIntegrationTestCase):

    def setUp(self):
        super().setUp()

        self.file_paths = [
            'assets/json/req0.txt',
            'assets/json/postgres.json',
            'assets/json/loco/kafka.json'
        ]

    def test_integration_response(self):
        """Test for integration jsons."""

        expected_data = [200, 200, 200]
        actual_data = []

        for item in self.file_paths:
            path = Path(
                __file__
            ).parent.parent.parent.parent / Path(item)

            resp = utils.send_request(
                self.server_path,
                path
            ).status_code
            actual_data.append(resp)

        self.assertListEqual(expected_data, actual_data, 'Полученные данные не совпадают')
