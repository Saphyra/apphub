package com.github.saphyra.apphub.service.custom.elite_base.service.power;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.Power;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.PowerplayState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PowerControllerImplTest {
    @InjectMocks
    private PowerControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    void getPowers() {
        assertThat(underTest.getPowers(accessTokenHeader)).hasSize(Power.values().length);
    }

    @Test
    void getPowerplayStates() {
        assertThat(underTest.getPowerplayStates(accessTokenHeader)).hasSize(PowerplayState.values().length);
    }
}