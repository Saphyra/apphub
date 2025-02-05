package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
class StarSystemConverterTest {
    private static final UUID ID = UUID.randomUUID();
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();
    private static final Long STAR_ID = 3432L;
    private static final String STAR_NAME = "star-name";
    private static final Double X_POS = 32432.245;
    private static final Double Y_POS = 565.65;
    private static final Double Z_POS = 23423.45645;
    private static final String ID_STRING = "id";
    private static final String LAST_UPDATE_STRING = "last-update";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DateTimeConverter dateTimeConverter;

    @InjectMocks
    private StarSystemConverter underTest;

    @Test
    void convertDomain() {
        StarSystem domain = StarSystem.builder()
            .id(ID)
            .lastUpdate(LAST_UPDATE)
            .starId(STAR_ID)
            .starName(STAR_NAME)
            .position(StarSystemPosition.builder()
                .x(X_POS)
                .y(Y_POS)
                .z(Z_POS)
                .build())
            .starType(StarType.M)
            .build();

        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(dateTimeConverter.convertDomain(LAST_UPDATE)).willReturn(LAST_UPDATE_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(ID_STRING, StarSystemEntity::getId)
            .returns(LAST_UPDATE_STRING, StarSystemEntity::getLastUpdate)
            .returns(STAR_ID, StarSystemEntity::getStarId)
            .returns(STAR_NAME, StarSystemEntity::getStarName)
            .returns(X_POS, StarSystemEntity::getXPos)
            .returns(Y_POS, StarSystemEntity::getYPos)
            .returns(Z_POS, StarSystemEntity::getZPos)
            .returns(StarType.M, StarSystemEntity::getStarType);
    }

    @Test
    void convertEntity() {
        StarSystemEntity domain = StarSystemEntity.builder()
            .id(ID_STRING)
            .lastUpdate(LAST_UPDATE_STRING)
            .starId(STAR_ID)
            .starName(STAR_NAME)
            .xPos(X_POS)
            .yPos(Y_POS)
            .zPos(Z_POS)
            .starType(StarType.M)
            .build();

        given(uuidConverter.convertEntity(ID_STRING)).willReturn(ID);
        given(dateTimeConverter.convertToLocalDateTime(LAST_UPDATE_STRING)).willReturn(LAST_UPDATE);

        assertThat(underTest.convertEntity(domain))
            .returns(ID, StarSystem::getId)
            .returns(LAST_UPDATE, StarSystem::getLastUpdate)
            .returns(STAR_ID, StarSystem::getStarId)
            .returns(STAR_NAME, StarSystem::getStarName)
            .returns(X_POS, starSystem -> starSystem.getPosition().getX())
            .returns(Y_POS, starSystem -> starSystem.getPosition().getY())
            .returns(Z_POS, starSystem -> starSystem.getPosition().getZ())
            .returns(StarType.M, StarSystem::getStarType);
    }
}