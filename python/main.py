from __future__ import print_function

import logging
import time

import grpc

import person_service_pb2
import person_service_pb2_grpc


def run():
    with grpc.insecure_channel('localhost:8001') as channel:
        stub = person_service_pb2_grpc.PersonServiceStub(channel)

        for x in range(100):
            response = stub.List(person_service_pb2.ListRequest())
            print("Client received list: " + response.person[0].name)
            time.sleep(1)

if __name__ == '__main__':
    logging.basicConfig()
    run()
