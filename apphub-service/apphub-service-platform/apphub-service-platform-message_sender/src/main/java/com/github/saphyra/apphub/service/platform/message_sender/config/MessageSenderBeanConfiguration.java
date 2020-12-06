package com.github.saphyra.apphub.service.platform.message_sender.config;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocaleMandatoryRequestValidation;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenFilterConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@Configuration
@EnableHealthCheck
@Import({
    AccessTokenFilterConfiguration.class
})
@EnableLocaleMandatoryRequestValidation
@EnableErrorHandler
@EnableWebSocket
class MessageSenderBeanConfiguration {
    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }
}
