package com.github.saphyra.apphub.lib.security.role;

import com.github.saphyra.apphub.lib.common_domain.WhiteListedEndpoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class RoleSetting {
    @NotNull
    private String pattern;

    @NotEmpty
    private List<String> methods;

    @NotEmpty
    private List<String> requiredRoles;
    private List<WhiteListedEndpoint> whitelistedEndpoints;
}
