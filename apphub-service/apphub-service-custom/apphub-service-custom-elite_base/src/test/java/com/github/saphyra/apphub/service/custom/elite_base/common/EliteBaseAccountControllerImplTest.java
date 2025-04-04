package com.github.saphyra.apphub.service.custom.elite_base.common;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
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
    private AccessTokenHeader accessTokenHeader;

    @Test
    void isAdmin_admin() {
        given(accessTokenHeader.getRoles()).willReturn(List.of(EliteBaseConstants.ROLE_ELITE_BASE_ADMIN));

        assertThat(underTest.isAdmin(accessTokenHeader)).isTrue();
    }

    @Test
    void isAdmin_notAdmin() {
        given(accessTokenHeader.getRoles()).willReturn(List.of());

        assertThat(underTest.isAdmin(accessTokenHeader)).isFalse();
    }
}