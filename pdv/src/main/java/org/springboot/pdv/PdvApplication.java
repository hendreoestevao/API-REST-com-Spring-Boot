package org.springboot.pdv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class PdvApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(PdvApplication.class, args);
    }

}
