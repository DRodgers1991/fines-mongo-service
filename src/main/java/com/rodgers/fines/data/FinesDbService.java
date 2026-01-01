package com.rodgers.fines.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class FinesDbService{

    public static void main(String[] args) {
        SpringApplication.run(FinesDbService.class, args);
    }
}
