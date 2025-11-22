package com.github.saphyra.apphub.lib.event.processor;

import com.github.saphyra.apphub.lib.common_util.SleepService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan
@ConditionalOnProperty(value = "event.processor.enabled", havingValue = "true", matchIfMissing = true)
class EventProcessorAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(SleepService.class)
    SleepService sleepService() {
        return new SleepService();
    }
}
