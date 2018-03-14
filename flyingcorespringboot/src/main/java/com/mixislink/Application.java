package com.mixislink;

import com.mixislink.builder.BuilderUtil;
import com.mixislink.service.Engine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class Application {
    public static void main(String[] args) throws Exception {
        BuilderUtil.startBuilder();
        Engine.ac = SpringApplication.run(Application.class, args);
    }
}
