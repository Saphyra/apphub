package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.reserved_storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
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
public class ReservedStorageConverterTest {
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 36;
    private static final String RESERVED_STORAGE_ID_STRING = "reserved-storage-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String LOCATION_TYPE = "location-type";
    private static final String LOCATION_STRING = "location";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ReservedStorageConverter underTest;

    @Test
    public void convertDomain() {
        ReservedStorageModel model = new ReservedStorageModel();
        model.setId(RESERVED_STORAGE_ID);
        model.setGameId(GAME_ID);
        model.setExternalReference(EXTERNAL_REFERENCE);
        model.setDataId(DATA_ID);
        model.setAmount(AMOUNT);
        model.setLocation(LOCATION);
        model.setLocationType(LOCATION_TYPE);

        given(uuidConverter.convertDomain(RESERVED_STORAGE_ID)).willReturn(RESERVED_STORAGE_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);

        ReservedStorageEntity result = underTest.convertDomain(model);

        assertThat(result.getReservedStorageId()).isEqualTo(RESERVED_STORAGE_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE_STRING);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getAmount()).isEqualTo(AMOUNT);
        assertThat(result.getLocation()).isEqualTo(LOCATION_STRING);
        assertThat(result.getLocationType()).isEqualTo(LOCATION_TYPE);
    }

    @Test
    public void convertEntity() {
        ReservedStorageEntity entity = ReservedStorageEntity.builder()
            .reservedStorageId(RESERVED_STORAGE_ID_STRING)
            .gameId(GAME_ID_STRING)
            .externalReference(EXTERNAL_REFERENCE_STRING)
            .location(LOCATION_STRING)
            .locationType(LOCATION_TYPE)
            .dataId(DATA_ID)
            .amount(AMOUNT)
            .build();

        given(uuidConverter.convertEntity(RESERVED_STORAGE_ID_STRING)).willReturn(RESERVED_STORAGE_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(EXTERNAL_REFERENCE_STRING)).willReturn(EXTERNAL_REFERENCE);
        given(uuidConverter.convertEntity(LOCATION_STRING)).willReturn(LOCATION);

        ReservedStorageModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(RESERVED_STORAGE_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getProcessType()).isEqualTo(GameItemType.RESERVED_STORAGE);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getAmount()).isEqualTo(AMOUNT);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getLocationType()).isEqualTo(LOCATION_TYPE);
    }
}