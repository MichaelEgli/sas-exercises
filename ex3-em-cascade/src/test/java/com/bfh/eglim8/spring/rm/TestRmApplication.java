package com.bfh.eglim8.spring.rm;

import org.springframework.boot.SpringApplication;

public class TestRmApplication {

	public static void main(String[] args) {
		SpringApplication.from(RmApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
