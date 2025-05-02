package com.example.searchingevents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SearchingEventsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchingEventsApplication.class, args);
	}

}
