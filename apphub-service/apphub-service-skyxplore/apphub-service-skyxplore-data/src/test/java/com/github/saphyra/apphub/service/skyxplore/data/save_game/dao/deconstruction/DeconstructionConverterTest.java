package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DeconstructionConverterTest {
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Integer CURRENT_WORK_POINTS = 243;
    private static final Integer PRIORITY = 24;
    private static final String DECONSTRUCTION_ID_STRING = "deconstruction-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String LOCATION_STRING = "location";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private DeconstructionConverter underTest;

    @Test
    void convertDomain() {
        DeconstructionModel model = new DeconstructionModel();
        model.setId(DECONSTRUCTION_ID);
        model.setGameId(GAME_ID);
        model.setExternalReference(EXTERNAL_REFERENCE);
        model.setLocation(LOCATION);
        model.setCurrentWorkPoints(CURRENT_WORK_POINTS);
        model.setPriority(PRIORITY);

        given(uuidConverter.convertDomain(DECONSTRUCTION_ID)).willReturn(DECONSTRUCTION_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);

        DeconstructionEntity result = underTest.convertDomain(model);

        assertThat(result.getDeconstructionId()).isEqualTo(DECONSTRUCTION_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE_STRING);
        assertThat(result.getLocation()).isEqualTo(LOCATION_STRING);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
    }

    @Test
    void convertEntity() {
        DeconstructionEntity entity = DeconstructionEntity.builder()
            .deconstructionId(DECONSTRUCTION_ID_STRING)
            .gameId(GAME_ID_STRING)
            .externalReference(EXTERNAL_REFERENCE_STRING)
            .location(LOCATION_STRING)
            .currentWorkPoints(CURRENT_WORK_POINTS)
            .priority(PRIORITY)
            .build();

        given(uuidConverter.convertEntity(DECONSTRUCTION_ID_STRING)).willReturn(DECONSTRUCTION_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(EXTERNAL_REFERENCE_STRING)).willReturn(EXTERNAL_REFERENCE);
        given(uuidConverter.convertEntity(LOCATION_STRING)).willReturn(LOCATION);

        DeconstructionModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(DECONSTRUCTION_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
    }
}