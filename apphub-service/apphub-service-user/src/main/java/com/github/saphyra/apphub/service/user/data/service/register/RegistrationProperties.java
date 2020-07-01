package com.github.saphyra.apphub.service.user.data.service.register;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "registration")
@Data
public class RegistrationProperties {
    private List<String> defaultRoles;
}
