package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.allocated_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
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
public class AllocatedResourceConverterTest {
    private static final UUID ALLOCATED_RESOURCE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String LOCATION_TYPE = "location-type";
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 234;
    private static final String ALLOCATED_RESOURCE_ID_STRING = "allocated-resource-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String LOCATION_STRING = "location";
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private AllocatedResourceConverter underTest;

    @Test
    public void convertEntity() {
        AllocatedResourceEntity entity = AllocatedResourceEntity.builder()
            .allocatedResourceId(ALLOCATED_RESOURCE_ID_STRING)
            .gameId(GAME_ID_STRING)
            .location(LOCATION_STRING)
            .locationType(LOCATION_TYPE)
            .externalReference(EXTERNAL_REFERENCE_STRING)
            .dataId(DATA_ID)
            .amount(AMOUNT)
            .build();

        given(uuidConverter.convertEntity(ALLOCATED_RESOURCE_ID_STRING)).willReturn(ALLOCATED_RESOURCE_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(LOCATION_STRING)).willReturn(LOCATION);
        given(uuidConverter.convertEntity(EXTERNAL_REFERENCE_STRING)).willReturn(EXTERNAL_REFERENCE);

        AllocatedResourceModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(ALLOCATED_RESOURCE_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.ALLOCATED_RESOURCE);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getLocationType()).isEqualTo(LOCATION_TYPE);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getAmount()).isEqualTo(AMOUNT);
    }

    @Test
    public void convertDomain() {
        AllocatedResourceModel model = new AllocatedResourceModel();
        model.setId(ALLOCATED_RESOURCE_ID);
        model.setGameId(GAME_ID);
        model.setLocation(LOCATION);
        model.setLocationType(LOCATION_TYPE);
        model.setExternalReference(EXTERNAL_REFERENCE);
        model.setDataId(DATA_ID);
        model.setAmount(AMOUNT);

        given(uuidConverter.convertDomain(ALLOCATED_RESOURCE_ID)).willReturn(ALLOCATED_RESOURCE_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);
        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);

        AllocatedResourceEntity result = underTest.convertDomain(model);

        assertThat(result.getAllocatedResourceId()).isEqualTo(ALLOCATED_RESOURCE_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getLocation()).isEqualTo(LOCATION_STRING);
        assertThat(result.getLocationType()).isEqualTo(LOCATION_TYPE);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE_STRING);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getAmount()).isEqualTo(AMOUNT);
    }
}