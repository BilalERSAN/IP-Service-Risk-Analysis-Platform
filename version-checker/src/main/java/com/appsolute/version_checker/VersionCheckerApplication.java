package com.appsolute.version_checker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;


@SpringBootApplication
@EnableKafka
public class VersionCheckerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VersionCheckerApplication.class, args);
	}

}
