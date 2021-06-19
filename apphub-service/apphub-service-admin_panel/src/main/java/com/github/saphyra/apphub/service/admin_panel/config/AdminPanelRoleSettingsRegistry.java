package com.github.saphyra.apphub.service.admin_panel.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.lib.security.role.RoleFilterSettingRegistry;
import com.github.saphyra.apphub.lib.security.role.RoleSetting;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "role")
@EnableConfigurationProperties
@Data
public class AdminPanelRoleSettingsRegistry implements RoleFilterSettingRegistry {
    private List<RoleSetting> settings;
}
