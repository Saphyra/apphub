package com.github.saphyra.apphub.lib.security.role;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "role")
@EnableConfigurationProperties
@Data
@Validated
public class RoleFilterSettingRegistry {
    @NotEmpty
    private List<RoleSetting> settings;
}
