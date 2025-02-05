package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StarSystemFactoryTest {
    private static final UUID ID = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final Long STAR_ID = 23423L;
    private static final String STAR_NAME = "star-name";
    private static final Double X_POS = 32423.34;
    private static final Double Y_POS = 23.32;
    private static final Double Z_POS = 3565.564;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private StarSystemFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(ID);
        Double[] starPosition = {X_POS, Y_POS, Z_POS};

        assertThat(underTest.create(LAST_UPDATE, STAR_ID, STAR_NAME, starPosition, StarType.A))
            .returns(ID, StarSystem::getId)
            .returns(LAST_UPDATE, StarSystem::getLastUpdate)
            .returns(STAR_ID, StarSystem::getStarId)
            .returns(STAR_NAME, StarSystem::getStarName)
            .returns(StarSystemPosition.parse(starPosition), StarSystem::getPosition)
            .returns(StarType.A, StarSystem::getStarType);
    }
}