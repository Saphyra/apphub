package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.stored_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
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
public class StoredResourceConverterTest {
    private static final UUID STORED_RESOURCE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 25;
    private static final String STORED_RESOURCE_ID_STRING = "stored-resource-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String LOCATION_STRING = "location";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private StoredResourceConverter underTest;

    @Test
    public void convertDomain() {
        StoredResourceModel model = new StoredResourceModel();
        model.setId(STORED_RESOURCE_ID);
        model.setGameId(GAME_ID);
        model.setLocation(LOCATION);
        model.setDataId(DATA_ID);
        model.setAmount(AMOUNT);

        given(uuidConverter.convertDomain(STORED_RESOURCE_ID)).willReturn(STORED_RESOURCE_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);

        StoredResourceEntity result = underTest.convertDomain(model);

        assertThat(result.getStoredResourceId()).isEqualTo(STORED_RESOURCE_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getLocation()).isEqualTo(LOCATION_STRING);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getAmount()).isEqualTo(AMOUNT);
    }

    @Test
    public void convertEntity() {
        StoredResourceEntity entity = StoredResourceEntity.builder()
            .storedResourceId(STORED_RESOURCE_ID_STRING)
            .gameId(GAME_ID_STRING)
            .location(LOCATION_STRING)
            .dataId(DATA_ID)
            .amount(AMOUNT)
            .build();

        given(uuidConverter.convertEntity(STORED_RESOURCE_ID_STRING)).willReturn(STORED_RESOURCE_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(LOCATION_STRING)).willReturn(LOCATION);

        StoredResourceModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(STORED_RESOURCE_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.STORED_RESOURCE);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getAmount()).isEqualTo(AMOUNT);
    }
}