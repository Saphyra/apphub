package com.github.saphyra.apphub.service.platform.web_content;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorTranslation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableErrorTranslation
public class WebContentBeanConfiguration {
    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }

    @Bean
    ClassLoaderTemplateResolver thymeLeafTemplateResolverConfig() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("html/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(0);

        return templateResolver;
    }

    @Bean
     ContentLoaderFactory contentLoaderFactory(
        ObjectMapper objectMapper,
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver
    ) {
        return new ContentLoaderFactory(objectMapper, pathMatchingResourcePatternResolver);
    }

    @Bean
     PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver() {
        return new PathMatchingResourcePatternResolver();
    }
}
