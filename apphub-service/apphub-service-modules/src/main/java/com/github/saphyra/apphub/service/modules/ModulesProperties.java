package com.github.saphyra.apphub.service.modules;

import com.github.saphyra.apphub.service.modules.domain.Module;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@EnableConfigurationProperties
@ConfigurationProperties(prefix = "modules")
@Configuration
@Data
@Validated
public class ModulesProperties {
    @NotNull
    private Map<String, List<Module>> modules;
}
