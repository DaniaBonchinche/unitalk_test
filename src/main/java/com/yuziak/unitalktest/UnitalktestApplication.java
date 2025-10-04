package com.yuziak.unitalktest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class UnitalktestApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnitalktestApplication.class, args);
	}

}
