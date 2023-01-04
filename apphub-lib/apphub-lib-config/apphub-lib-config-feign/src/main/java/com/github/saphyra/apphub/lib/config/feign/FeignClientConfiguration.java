package com.github.saphyra.apphub.lib.config.feign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients("com.github.saphyra.apphub.api")
public class FeignClientConfiguration {
}
