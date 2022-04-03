package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.durability_item;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class DurabilityItemConverterTest {
    private static final UUID DURABILITY_ITEM_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final String METADATA = "metadata";
    private static final String DATA_ID = "data-id";
    private static final Integer MAX_DURABILITY = 364;
    private static final Integer CURRENT_DURABILITY = 43;
    private static final String DURABILITY_ITEM_ID_STRING = "durability-item-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String PARENT_STRING = "parent";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private DurabilityItemConverter underTest;

    @Test
    public void convertEntity() {
        DurabilityItemEntity entity = DurabilityItemEntity.builder()
            .durabilityItemId(DURABILITY_ITEM_ID_STRING)
            .gameId(GAME_ID_STRING)
            .parent(PARENT_STRING)
            .metadata(METADATA)
            .maxDurability(MAX_DURABILITY)
            .currentDurability(CURRENT_DURABILITY)
            .dataId(DATA_ID)
            .build();

        given(uuidConverter.convertEntity(DURABILITY_ITEM_ID_STRING)).willReturn(DURABILITY_ITEM_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(PARENT_STRING)).willReturn(PARENT);

        DurabilityItemModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(DURABILITY_ITEM_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.DURABILITY_ITEM_MODEL);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getMetadata()).isEqualTo(METADATA);
        assertThat(result.getMaxDurability()).isEqualTo(MAX_DURABILITY);
        assertThat(result.getCurrentDurability()).isEqualTo(CURRENT_DURABILITY);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
    }

    @Test
    public void convertDomain() {
        DurabilityItemModel model = new DurabilityItemModel();
        model.setId(DURABILITY_ITEM_ID);
        model.setGameId(GAME_ID);
        model.setParent(PARENT);
        model.setMetadata(METADATA);
        model.setDataId(DATA_ID);
        model.setMaxDurability(MAX_DURABILITY);
        model.setCurrentDurability(CURRENT_DURABILITY);

        given(uuidConverter.convertDomain(DURABILITY_ITEM_ID)).willReturn(DURABILITY_ITEM_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);

        DurabilityItemEntity result = underTest.convertDomain(model);

        assertThat(result.getDurabilityItemId()).isEqualTo(DURABILITY_ITEM_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getParent()).isEqualTo(PARENT_STRING);
        assertThat(result.getMetadata()).isEqualTo(METADATA);
        assertThat(result.getMaxDurability()).isEqualTo(MAX_DURABILITY);
        assertThat(result.getCurrentDurability()).isEqualTo(CURRENT_DURABILITY);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
    }
}