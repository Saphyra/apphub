package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.authorization;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "role")
@EnableConfigurationProperties
@Data
@Validated
class RoleProperties {
    @NotEmpty
    private List<RoleSetting> settings;
}
