package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionAreaModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
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
class ConstructionAreaConverterTest {
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final String CONSTRUCTION_AREA_ID_STRING = "construction-area-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String SURFACE_ID_STRING = "surface-id";
    private static final String LOCATION_STRING = "location";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ConstructionAreaConverter underTest;

    @Test
    void convertDomain() {
        ConstructionAreaModel domain = new ConstructionAreaModel();
        domain.setId(CONSTRUCTION_AREA_ID);
        domain.setGameId(GAME_ID);
        domain.setSurfaceId(SURFACE_ID);
        domain.setLocation(LOCATION);
        domain.setDataId(DATA_ID);

        given(uuidConverter.convertDomain(CONSTRUCTION_AREA_ID)).willReturn(CONSTRUCTION_AREA_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(SURFACE_ID)).willReturn(SURFACE_ID_STRING);
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(CONSTRUCTION_AREA_ID_STRING, ConstructionAreaEntity::getConstructionAreaId)
            .returns(GAME_ID_STRING, ConstructionAreaEntity::getGameId)
            .returns(SURFACE_ID_STRING, ConstructionAreaEntity::getSurfaceId)
            .returns(LOCATION_STRING, ConstructionAreaEntity::getLocation)
            .returns(DATA_ID, ConstructionAreaEntity::getDataId);
    }

    @Test
    void convertEntity() {
        ConstructionAreaEntity entity = ConstructionAreaEntity.builder()
            .constructionAreaId(CONSTRUCTION_AREA_ID_STRING)
            .gameId(GAME_ID_STRING)
            .surfaceId(SURFACE_ID_STRING)
            .location(LOCATION_STRING)
            .dataId(DATA_ID)
            .build();

        given(uuidConverter.convertEntity(CONSTRUCTION_AREA_ID_STRING)).willReturn(CONSTRUCTION_AREA_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(SURFACE_ID_STRING)).willReturn(SURFACE_ID);
        given(uuidConverter.convertEntity(LOCATION_STRING)).willReturn(LOCATION);

        assertThat(underTest.convertEntity(entity))
            .returns(CONSTRUCTION_AREA_ID, GameItem::getId)
            .returns(GAME_ID, ConstructionAreaModel::getGameId)
            .returns(SURFACE_ID, ConstructionAreaModel::getSurfaceId)
            .returns(LOCATION, ConstructionAreaModel::getLocation)
            .returns(DATA_ID, ConstructionAreaModel::getDataId)
            .returns(GameItemType.CONSTRUCTION_AREA, GameItem::getType);
    }
}