package com.github.saphyra.apphub.ci.value;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "defaults")
@Data
public class DefaultProperties {
    private String defaultLocale;
    @Deprecated
    private DeployMode localDeployMode;
    @Deprecated
    private DeployMode remoteDeployMode;
    //TODO rename to buildThreadCount
    private Integer buildThreadCountDefault;
    @Deprecated
    private Integer buildThreadCountSkipTests;
    //TODO rename to testThreadCount
    private Integer localRunTestsThreadCount;
    private Integer remoteTestsThreadCount;
    //TODO rename to serviceStartupCountLimit
    private Integer localServiceStartupCountLimit;
    @Deprecated
    private Integer remoteServiceStartupCountLimit;
    //TODO rename to preCreateDriverCount
    private Integer localRunTestsPreCreateDriverCount;
    @Deprecated
    private Integer remoteRunTestsPreCreateDriverCount;
    private String bashFileLocation;
    private Integer browserStartupLimit;
    private Boolean guiEnabled;
    //TODO rename to testRetryCount
    private Integer integrationRetryCount;
}
