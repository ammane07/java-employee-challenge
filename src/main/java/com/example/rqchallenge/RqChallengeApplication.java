package com.example.rqchallenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RqChallengeApplication {
    private static Logger logger = LoggerFactory.getLogger(RqChallengeApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RqChallengeApplication.class, args);
        logger.info("Application started successfully");
    }

}
