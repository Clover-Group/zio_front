import json
import typing
import requests


def send_request(
        url: typing.Text,
        file_path: typing.Text,
) -> requests.Response:
    """
    Method for sending request to server.
    Args:
        url: path to request
        file_path: data for request

    Returns: response from server

    """

    with open(file_path, 'r') as json_data:
        request_data = json.load(json_data)

    return requests.post(url, json=request_data, headers={"Content-Type": "application/json"})
