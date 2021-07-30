package com.example.helloworld.grpc;

import com.example.helloworld.grpc.gencode.HelloRequest;
import com.example.helloworld.grpc.gencode.HelloResponse;
import com.example.helloworld.grpc.gencode.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @Author: 进击的烧年.
 * @Date: 2021/7/30 10:46
 * @Description:
 */
public class GrpcClient {
    public static void main(String[] args) {
        ManagedChannel channel = null;
        try {
            channel = ManagedChannelBuilder
                    .forAddress("localhost", 50051)
                    .usePlaintext().build();

            HelloServiceGrpc.HelloServiceBlockingStub stub = HelloServiceGrpc.newBlockingStub(channel);

            HelloResponse helloResponse = stub
                    .hello(HelloRequest.newBuilder()
                            .setFirstName("Baeldung")
                            .setLastName("gRPC")
                            .build());

            System.out.println(helloResponse.getGreeting());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                try {
                    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
