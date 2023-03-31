package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.durability;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityModel;
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
public class DurabilityItemConverterTest {
    private static final UUID DURABILITY_ITEM_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final Integer MAX_HIT_POINTS = 364;
    private static final Integer CURRENT_HIT_POINTS = 43;
    private static final String DURABILITY_ID_STRING = "durability-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private DurabilityItemConverter underTest;

    @Test
    public void convertEntity() {
        DurabilityEntity entity = DurabilityEntity.builder()
            .durabilityId(DURABILITY_ID_STRING)
            .gameId(GAME_ID_STRING)
            .externalReference(EXTERNAL_REFERENCE_STRING)
            .maxHitPoints(MAX_HIT_POINTS)
            .currentHitPoints(CURRENT_HIT_POINTS)
            .build();

        given(uuidConverter.convertEntity(DURABILITY_ID_STRING)).willReturn(DURABILITY_ITEM_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(EXTERNAL_REFERENCE_STRING)).willReturn(EXTERNAL_REFERENCE);

        DurabilityModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(DURABILITY_ITEM_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.DURABILITY);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getMaxHitPoints()).isEqualTo(MAX_HIT_POINTS);
        assertThat(result.getCurrentHitPoints()).isEqualTo(CURRENT_HIT_POINTS);
    }

    @Test
    public void convertDomain() {
        DurabilityModel model = new DurabilityModel();
        model.setId(DURABILITY_ITEM_ID);
        model.setGameId(GAME_ID);
        model.setExternalReference(EXTERNAL_REFERENCE);
        model.setMaxHitPoints(MAX_HIT_POINTS);
        model.setCurrentHitPoints(CURRENT_HIT_POINTS);

        given(uuidConverter.convertDomain(DURABILITY_ITEM_ID)).willReturn(DURABILITY_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);

        DurabilityEntity result = underTest.convertDomain(model);

        assertThat(result.getDurabilityId()).isEqualTo(DURABILITY_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE_STRING);
        assertThat(result.getMaxHitPoints()).isEqualTo(MAX_HIT_POINTS);
        assertThat(result.getCurrentHitPoints()).isEqualTo(CURRENT_HIT_POINTS);
    }
}