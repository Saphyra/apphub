package com.github.saphyra.apphub.service.feature.elite_base.common;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EliteBaseAccountControllerImplTest {
    @InjectMocks
    private EliteBaseAccountControllerImpl underTest;

    @Mock
    private AccessToken accessToken;

    @Test
    void isAdmin_admin() {
        given(accessToken.getRoles()).willReturn(List.of(EliteBaseConstants.ROLE_ELITE_BASE_ADMIN));

        assertThat(underTest.isAdmin(accessToken)).isTrue();
    }

    @Test
    void isAdmin_notAdmin() {
        given(accessToken.getRoles()).willReturn(List.of());

        assertThat(underTest.isAdmin(accessToken)).isFalse();
    }
}