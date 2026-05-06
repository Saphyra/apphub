package com.github.saphrya.apphub.service.platform.authorization.etc;

import com.github.saphyra.apphub.api.etc.user.client.AuthorizationClient;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationRequest;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationClientProxy {
    private final AuthorizationClient authorizationClient;

    public AuthorizationResponse authorize(AuthorizationRequest request) {
        return authorizationClient.authorize(request);
    }

    public List<String> getRoles(UUID userId) {
        return authorizationClient.getRoles(userId);
    }
}
