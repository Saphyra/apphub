package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
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
public class ConstructionConverterTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 245;
    private static final Integer CURRENT_WORK_POINTS = 3465;
    private static final Integer PRIORITY = 5726;
    private static final String CONSTRUCTION_ID_STRING = "construction-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String LOCATION_STRING = "location";
    private static final String DATA = "data";
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ConstructionConverter underTest;

    @Test
    public void convertDomain() {
        ConstructionModel model = new ConstructionModel();
        model.setId(CONSTRUCTION_ID);
        model.setGameId(GAME_ID);
        model.setExternalReference(EXTERNAL_REFERENCE);
        model.setLocation(LOCATION);
        model.setRequiredWorkPoints(REQUIRED_WORK_POINTS);
        model.setCurrentWorkPoints(CURRENT_WORK_POINTS);
        model.setPriority(PRIORITY);
        model.setData(DATA);
        model.setConstructionType(ConstructionType.CONSTRUCTION);

        given(uuidConverter.convertDomain(CONSTRUCTION_ID)).willReturn(CONSTRUCTION_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);

        ConstructionEntity result = underTest.convertDomain(model);

        assertThat(result.getConstructionId()).isEqualTo(CONSTRUCTION_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getConstructionType()).isEqualTo(ConstructionType.CONSTRUCTION.name());
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE_STRING);
        assertThat(result.getLocation()).isEqualTo(LOCATION_STRING);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
        assertThat(result.getData()).isEqualTo(DATA);
    }

    @Test
    public void convertEntity() {
        ConstructionEntity entity = new ConstructionEntity();
        entity.setConstructionId(CONSTRUCTION_ID_STRING);
        entity.setGameId(GAME_ID_STRING);
        entity.setExternalReference(EXTERNAL_REFERENCE_STRING);
        entity.setLocation(LOCATION_STRING);
        entity.setRequiredWorkPoints(REQUIRED_WORK_POINTS);
        entity.setCurrentWorkPoints(CURRENT_WORK_POINTS);
        entity.setPriority(PRIORITY);
        entity.setConstructionType(ConstructionType.CONSTRUCTION.name());
        entity.setData(DATA);

        given(uuidConverter.convertEntity(CONSTRUCTION_ID_STRING)).willReturn(CONSTRUCTION_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(EXTERNAL_REFERENCE_STRING)).willReturn(EXTERNAL_REFERENCE);
        given(uuidConverter.convertEntity(LOCATION_STRING)).willReturn(LOCATION);

        ConstructionModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(CONSTRUCTION_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.CONSTRUCTION);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getConstructionType()).isEqualTo(ConstructionType.CONSTRUCTION);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
        assertThat(result.getData()).isEqualTo(DATA);
    }
}