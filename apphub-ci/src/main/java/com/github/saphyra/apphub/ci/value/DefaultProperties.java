package com.github.saphyra.apphub.ci.value;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "defaults")
@Data
public class DefaultProperties {
    private LocalRunMode localRunMode;
    private Integer localRunThreadCountDefault;
    private Integer localRunThreadCountSkipTests;
    private Integer localRunTestsThreadCount;
}
