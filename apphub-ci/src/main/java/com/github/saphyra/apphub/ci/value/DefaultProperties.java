package com.github.saphyra.apphub.ci.value;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "defaults")
@Data
public class DefaultProperties {
    private String defaultLocale;
    private DeployMode localDeployMode;
    private DeployMode remoteDeployMode;
    private Integer buildThreadCountDefault;
    private Integer buildThreadCountSkipTests;
    private Integer localRunTestsThreadCount;
    private Integer remoteTestsThreadCount;
    private Integer localServiceStartupCountLimit;
    private Integer localRunTestsPreCreateDriverCount;
    private Integer remoteRunTestsPreCreateDriverCount;
    private String bashFileLocation;
}
