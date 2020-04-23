package com.github.saphyra.apphub.service.platform.main_gateway.service.locale;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@ConfigurationProperties
@Component
@Data
@Validated
class SupportedLocales {
    @NotNull
    private List<String> supportedLocales;

    boolean isSupported(String locale) {
        return supportedLocales.contains(locale);
    }
}
