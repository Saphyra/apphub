package com.github.saphrya.apphub.service.platform.authorization.etc;

import com.github.saphyra.apphub.api.etc.user.client.AuthorizationClient;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationRequest;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthorizationClientProxyTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private AuthorizationClient authorizationClient;

    @InjectMocks
    private AuthorizationClientProxy underTest;

    @Mock
    private AuthorizationRequest authorizationRequest;

    @Mock
    private AuthorizationResponse authorizationResponse;

    @Test
    void authorize() {
        given(authorizationClient.authorize(authorizationRequest)).willReturn(authorizationResponse);

        AuthorizationResponse result = underTest.authorize(authorizationRequest);

        assertThat(result).isEqualTo(authorizationResponse);
    }

    @Test
    void getRoles() {
        List<String> roles = List.of("ROLE_A", "ROLE_B");
        given(authorizationClient.getRoles(USER_ID)).willReturn(roles);

        List<String> result = underTest.getRoles(USER_ID);

        assertThat(result).isEqualTo(roles);
    }
}