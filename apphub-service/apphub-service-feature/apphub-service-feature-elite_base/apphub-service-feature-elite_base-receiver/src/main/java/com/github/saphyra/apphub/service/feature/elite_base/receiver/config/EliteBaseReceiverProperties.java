package com.github.saphyra.apphub.service.feature.elite_base.receiver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@ConfigurationProperties("elite-base")
@Configuration
@Data
public class EliteBaseReceiverProperties {
    private Duration incomingMessageCheckInterval;
    private Duration incomingMessageTimeout;
    private boolean shutdownEnabled;
}
