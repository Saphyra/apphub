package com.github.saphyra.apphub.service.main_gateway;

import com.github.saphyra.apphub.lib.config.whielist.EnableWhiteListedEndpointProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableZuulProxy
@EnableWhiteListedEndpointProperties
@EnableFeignClients(basePackages = "com.github.saphyra.apphub.api")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
