import json
import typing
import unittest
from pathlib import Path

import lya
import requests

"""

This unittest helps to check base functionality of zio_front.

Designed by Andrey Romanov
Clover Group. 03/08/2019

"""


def send_request(
        url: typing.Text,
        file_path: typing.Text,
) -> typing.Dict[typing.Text, typing.Text]:
    """
    Method for sending request to zio_front.
    Args:
        url: path to request
        file_path: data for request

    Returns: response in json format

    """

    with open(file_path, 'r') as json_data:
        request_data = json.load(json_data)

    response = requests.post(url, json=request_data, headers={"Content-Type": "application/json"})

    return response.json()


class SimpleFrontTestCase(unittest.TestCase):
    """
    Simple Test Case for zio_front.
    """

    def setUp(self):

        config = lya.AttrDict.from_yaml('config.yml')

        self.server_url = 'http://{0}:{1}'.format(
            config['test_server']['host'],
            config['test_server']['port']
        )

        self.expected_data = {
            'data': 'test data'
        }

    def test_ch_processing(self):
        """Method with ClickHouse upload data."""

        file_path = Path(__file__).parent.parent / Path('assets/json/req0.txt')
        server_path = '{0}/tsp_processing'.format(self.server_url)

        actual_data = send_request(server_path, file_path)

        self.assertEqual(self.expected_data, actual_data, 'Полученные данные не совпадают')

    def test_kafka_processing(self):
        """Method with Kafka upload data."""

        file_path = Path(__file__).parent.parent / Path('assets/json/loco/kafka.json')
        server_path = '{0}/tsp_processing'.format(self.server_url)

        actual_data = send_request(server_path, file_path)

        self.assertEqual(self.expected_data, actual_data, 'Полученные данные не совпадают')


