package com.ayacodes.studentspace;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootApplication
public class StudentSpaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentSpaceApplication.class, args);
    }

}
