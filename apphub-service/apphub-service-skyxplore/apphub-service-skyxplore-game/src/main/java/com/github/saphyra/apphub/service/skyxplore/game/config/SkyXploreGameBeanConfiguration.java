package com.github.saphyra.apphub.service.skyxplore.game.config;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.common_util.RomanNumberConverter;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.geometry.CrossCalculator;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocaleMandatoryRequestValidation;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenFilterConfiguration;
import com.github.saphyra.apphub.lib.security.role.RoleFilterConfiguration;
import com.github.saphyra.apphub.lib.skyxplore.data.SkyXploreDataConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Configuration
@EnableHealthCheck
@Import({
    AccessTokenFilterConfiguration.class,
    RoleFilterConfiguration.class,
    SkyXploreDataConfig.class
})
@EnableLocaleMandatoryRequestValidation
@EnableErrorHandler
public class SkyXploreGameBeanConfiguration {
    @Bean
    ExecutorServiceBean executorServiceBean(SleepService sleepService) {
        return new ExecutorServiceBean(sleepService);
    }

    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Bean
    Random random() {
        return new Random();
    }

    @Bean
    RomanNumberConverter romanNumberConverter() {
        return new RomanNumberConverter();
    }

    @Bean
    DistanceCalculator distanceCalculator() {
        return new DistanceCalculator();
    }

    @Bean
    CrossCalculator crossCalculator(DistanceCalculator distanceCalculator) {
        return new CrossCalculator(distanceCalculator);
    }

    @Bean
    SleepService sleepService() {
        return new SleepService();
    }

    @Bean
    BlockingQueue<SkyXploreGameCreationRequest> gameCreationQueue() {
        return new ArrayBlockingQueue<>(100);//TODO config value
    }
}
