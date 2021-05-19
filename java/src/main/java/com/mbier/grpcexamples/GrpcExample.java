package com.mbier.grpcexamples;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mbier.grpcexamples.protos.ListRequest;
import com.mbier.grpcexamples.protos.ListResponse;
import com.mbier.grpcexamples.protos.PersonServiceGrpc;

public class GrpcExample {
  private static final Logger logger = Logger.getLogger(GrpcExample.class.getName());

  private final PersonServiceGrpc.PersonServiceBlockingStub blockingStub;
  private final PersonServiceGrpc.PersonServiceStub asyncStub;

  public GrpcExample(Channel channel) {
    this.blockingStub = PersonServiceGrpc.newBlockingStub(channel);
    this.asyncStub = PersonServiceGrpc.newStub(channel);
  }

  public void list() {
    ListRequest request = ListRequest.newBuilder().build();
    ListResponse response;
    try {
      response = blockingStub.list(request);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
    System.out.println("Recive List: " + response.getPersonList().toString());
  }

  public void listStream() {
    ListRequest request = ListRequest.newBuilder().build();
    try {
      this.asyncStub.listStream(request, new StreamObserver<ListResponse>() {
        @Override
        public void onNext(ListResponse r) {
          System.out.println("Recive ListStream");
          r.getPersonList().forEach(p -> {
            System.out.println("Recive ListStream - person: " + p.getName());
          });
        }

        @Override
        public void onError(Throwable t) {
          logger.log(Level.WARNING, "RPC failed: {0}", t);
        }

        @Override
        public void onCompleted() {
          logger.log(Level.INFO, "RPC onCompletet");
        }
      });

    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
  }

  public static void main(String[] args) throws Exception {
    logger.log(Level.INFO, "Start Client");

    String target = "localhost:8001";

    ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
    try {
      GrpcExample client = new GrpcExample(channel);
      client.list();
      Thread.sleep(1000);
      client.listStream();
    } finally {
      channel.shutdown().awaitTermination(60, TimeUnit.SECONDS);
      logger.log(Level.INFO, "End Client");
    }
  }
}