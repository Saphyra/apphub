package com.github.saphyra.apphub.service.utils.config;

import com.github.saphyra.apphub.lib.security.role.RoleFilterSettingRegistry;
import com.github.saphyra.apphub.lib.security.role.RoleSetting;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "role")
@EnableConfigurationProperties
@Data
public class RoleSettings implements RoleFilterSettingRegistry {
    private List<RoleSetting> settings;
}
