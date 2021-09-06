package com.kai;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.kai.dao")
@Slf4j
@EnableCaching
public class SpringbootShiroJwtRedisApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(SpringbootShiroJwtRedisApplication.class, args);
        log.info("http://localhost:8081/");
    }

}
