package com.github.saphyra.apphub.service.user.data.service.role;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties(prefix = "add-role-to-all")
@Configuration
@Data
public class AddRoleToAllProperties {
    private List<String> restrictedRoles;
}
