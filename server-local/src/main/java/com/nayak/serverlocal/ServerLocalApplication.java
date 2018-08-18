package com.nayak.serverlocal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.dataflow.server.EnableDataFlowServer;


@EnableDataFlowServer
@SpringBootApplication
public class ServerLocalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerLocalApplication.class, args);
	}
}
