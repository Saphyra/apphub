package com.github.saphyra.apphub.service.user.disabled_role;

import com.github.saphyra.apphub.lib.common_domain.Role;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties
@Data
@Configuration
class DisabledRoleProperties {
    private List<Role> rolesCanBeDisabled;
}
