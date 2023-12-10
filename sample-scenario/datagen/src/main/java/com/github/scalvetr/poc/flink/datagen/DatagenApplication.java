package com.github.scalvetr.poc.flink.datagen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DatagenApplication implements CommandLineRunner {
	private static Logger log = LoggerFactory
			.getLogger(DatagenApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(DatagenApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		log.info("EXECUTING : command line runner");

		for (int i = 0; i < args.length; ++i) {
			log.info("args[{}]: {}", i, args[i]);
		}

	}
}
