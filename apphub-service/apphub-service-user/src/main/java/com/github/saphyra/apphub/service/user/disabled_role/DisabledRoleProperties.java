package com.github.saphyra.apphub.service.user.disabled_role;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties
@Data
@Configuration
class DisabledRoleProperties {
    private List<String> rolesCanBeDisabled;
}
