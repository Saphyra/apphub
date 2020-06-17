package com.github.saphyra.apphub.lib.security.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleSetting {
    private String pattern;
    private List<String> methods;
    private List<String> requiredRoles;
}
