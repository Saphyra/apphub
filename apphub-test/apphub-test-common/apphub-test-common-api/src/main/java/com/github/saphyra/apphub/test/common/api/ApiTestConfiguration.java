package com.github.saphyra.apphub.test.common.api;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@ComponentScan(basePackages = "com.github.saphyra.apphub.service")
public class ApiTestConfiguration {

}
