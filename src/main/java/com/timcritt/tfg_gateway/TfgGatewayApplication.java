package com.timcritt.tfg_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TfgGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(TfgGatewayApplication.class, args);
	}

}
