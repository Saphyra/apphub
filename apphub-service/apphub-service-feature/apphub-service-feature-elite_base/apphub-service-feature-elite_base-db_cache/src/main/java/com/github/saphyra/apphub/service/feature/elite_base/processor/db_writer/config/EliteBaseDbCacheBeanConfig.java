package com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.config;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.config.rabbitmq.message.elite_base.backfill.EliteBaseBackfillRabbitMqConsumerConfig;
import com.github.saphyra.apphub.lib.config.rabbitmq.message.elite_base.incoming.EliteBaseIncomingRabbitMqConsumerConfig;
import com.github.saphyra.apphub.lib.config.rabbitmq.message.elite_base.stored.EliteBaseStoredRabbitMqPublisherConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({EliteBaseIncomingRabbitMqConsumerConfig.class, EliteBaseStoredRabbitMqPublisherConfig.class, EliteBaseBackfillRabbitMqConsumerConfig.class})
public class EliteBaseDbCacheBeanConfig {
    @Bean
    UuidConverter uuidConverter(){
        return new UuidConverter();
    }

    @Bean
    DateTimeConverter dateTimeConverter() {
        return new DateTimeConverter();
    }
}
