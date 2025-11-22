package com.github.saphyra.apphub.lib.config.feign;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@AutoConfiguration
@EnableFeignClients("com.github.saphyra.apphub.api")
public class FeignClientAutoConfiguration {
}
