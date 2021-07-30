package com.example.helloworld.grpc;

import com.example.helloworld.grpc.gencode.HelloRequest;
import com.example.helloworld.grpc.gencode.HelloResponse;
import com.example.helloworld.grpc.gencode.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;

/**
 * @Author: 进击的烧年.
 * @Date: 2021/7/30 10:39
 * @Description:
 */
public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {
    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {

        String greeting = new StringBuilder()
                .append("Hello, ")
                .append(request.getFirstName())
                .append(" ")
                .append(request.getLastName())
                .toString();

        HelloResponse response = HelloResponse.newBuilder()
                .setGreeting(greeting)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}