package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.api.user.model.login.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AccessTokenHeaderFactoryTest {
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ROLE = "role";

    @InjectMocks
    private AccessTokenHeaderFactory underTest;

    @Test
    public void create() {
        InternalAccessTokenResponse accessTokenResponse = InternalAccessTokenResponse.builder()
            .accessTokenId(ACCESS_TOKEN_ID)
            .userId(USER_ID)
            .roles(Arrays.asList(ROLE))
            .build();

        AccessTokenHeader result = underTest.create(accessTokenResponse);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getAccessTokenId()).isEqualTo(ACCESS_TOKEN_ID);
        assertThat(result.getRoles()).containsExactly(ROLE);
    }
}