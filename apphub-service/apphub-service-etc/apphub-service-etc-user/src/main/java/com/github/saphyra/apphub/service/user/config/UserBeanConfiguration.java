package com.github.saphyra.apphub.service.user.config;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.user.UserApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Slf4j
@EnableJpaRepositories(basePackageClasses = UserApplication.class)
@EntityScan(basePackageClasses = UserApplication.class)
@ComponentScan(basePackages = "com.github.saphyra.util")
class UserBeanConfiguration {
    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }

    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Bean
    @ConditionalOnMissingBean(SleepService.class)
    SleepService sleepService() {
        return new SleepService();
    }
}


