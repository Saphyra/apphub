package com.github.saphyra.apphub.api.etc.user.server;

import com.github.saphyra.apphub.api.etc.user.model.UserEndpoints;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationRequest;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationResponse;
import com.github.saphyra.apphub.lib.common_domain.Role;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

public interface UserAuthorizationController {
    @PostMapping(UserEndpoints.INTERNAL_AUTHORIZATION_AUTHORIZE)
    AuthorizationResponse authorize(@RequestBody AuthorizationRequest request);

    @GetMapping(UserEndpoints.INTERNAL_AUTHORIZATION_GET_ROLES)
    List<Role> getRoles(@PathVariable("userId") UUID userId);
}
