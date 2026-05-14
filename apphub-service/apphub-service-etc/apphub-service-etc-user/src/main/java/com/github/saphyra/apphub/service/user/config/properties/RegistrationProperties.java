package com.github.saphyra.apphub.service.user.config.properties;

import com.github.saphyra.apphub.lib.common_domain.Role;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "registration")
@Data
public class RegistrationProperties {
    private List<Role> defaultRoles;
}
