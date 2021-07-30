package com.example.helloworld.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

/**
 * @Author: 进击的烧年.
 * @Date: 2021/7/30 10:45
 * @Description:
 */
public class GrpcServer {
    public static void main(String[] args) {
        try {

            int port = 50051;
            final Server server = ServerBuilder.forPort(port)
                    .addService(new HelloServiceImpl())
                    .build()
                    .start();
            System.out.println("Server started, listening on " + port);
            server.awaitTermination();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
