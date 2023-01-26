package com.itmo.eva;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@MapperScan("com.itmo.eva.mapper")
@SpringBootApplication
public class EvaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvaApplication.class, args);
	}

}
