package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservedStorageLoaderTest {
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 254;

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private ReservedStorageLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private ReservedStorageModel model;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.RESERVED_STORAGE);
    }

    @Test
    void getArrayClass() {
        assertThat(underTest.getArrayClass()).isEqualTo(ReservedStorageModel[].class);
    }

    @Test
    void addToGameData() {
        given(gameData.getReservedStorages()).willReturn(reservedStorages);

        underTest.addToGameData(gameData, List.of(reservedStorage));

        verify(reservedStorages).addAll(List.of(reservedStorage));
    }

    @Test
    void convert() {
        given(model.getId()).willReturn(RESERVED_STORAGE_ID);
        given(model.getContainerId()).willReturn(LOCATION);
        given(model.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(model.getDataId()).willReturn(DATA_ID);
        given(model.getAmount()).willReturn(AMOUNT);

        ReservedStorage result = underTest.convert(model);

        assertThat(result.getReservedStorageId()).isEqualTo(RESERVED_STORAGE_ID);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getAmount()).isEqualTo(AMOUNT);
    }
}