package com.github.saphyra.apphub.service.skyxplore.game.config;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.concurrency.DefaultExecutorServiceBeanConfig;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.event.processor.EnableEventProcessor;
import com.github.saphyra.apphub.lib.geometry.CrossCalculator;
import com.github.saphyra.apphub.lib.geometry.DistanceCalculator;
import com.github.saphyra.apphub.lib.geometry.RandomCoordinateProvider;
import com.github.saphyra.apphub.lib.monitoring.EnableMemoryMonitoring;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocaleMandatoryRequestValidation;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenFilterConfiguration;
import com.github.saphyra.apphub.lib.skyxplore.data.SkyXploreDataConfig;
import com.github.saphyra.apphub.lib.web_socket.WebSocketConfiguration;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Configuration
@EnableHealthCheck
@Import({
    AccessTokenFilterConfiguration.class,
    SkyXploreDataConfig.class,
    DefaultExecutorServiceBeanConfig.class,
    WebSocketConfiguration.class
})
@EnableLocaleMandatoryRequestValidation
@EnableErrorHandler
@EnableEventProcessor
@EnableMemoryMonitoring
public class SkyXploreGameBeanConfiguration {
    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
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
    @ConditionalOnMissingBean(SleepService.class)
    SleepService sleepService() {
        return new SleepService();
    }

    @Bean
    BlockingQueue<BiWrapper<SkyXploreGameCreationRequest, UUID>> gameCreationQueue(GameProperties gameProperties) {
        return new ArrayBlockingQueue<>(gameProperties.getCreationQueueSize());
    }

    @Bean
    RandomCoordinateProvider randomCoordinateProvider(Random random) {
        return new RandomCoordinateProvider(random);
    }

    @Bean
    ApplicationContextProxy applicationContextProxy(ConfigurableApplicationContext applicationContext) {
        return new ApplicationContextProxy(applicationContext);
    }

    @Bean
    ObjectMapperWrapper objectMapperWrapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        return new ObjectMapperWrapper(objectMapper);
    }
}
