package com.github.saphyra.apphub.service.platform.web_content;

import com.github.saphyra.apphub.lib.config.access_token.AccessTokenConfiguration;
import com.github.saphyra.apphub.lib.monitoring.EnableMemoryMonitoring;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    AccessTokenConfiguration.class
})
@EnableMemoryMonitoring
public class WebContentBeanConfiguration {
}
