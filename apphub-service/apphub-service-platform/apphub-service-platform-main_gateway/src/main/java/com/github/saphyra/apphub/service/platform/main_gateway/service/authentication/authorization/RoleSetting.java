package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication.authorization;

import com.github.saphyra.apphub.lib.common_domain.WhiteListedEndpoint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
class RoleSetting {
    @NotNull
    private String pattern;

    @NotEmpty
    private List<String> methods;

    @NotEmpty
    private List<String> requiredRoles;
    private List<WhiteListedEndpoint> whitelistedEndpoints;
}
