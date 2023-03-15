package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ReservedStorageLoaderTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 246;

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private ReservedStorageLoader underTest;

    @Mock
    private ReservedStorageModel reservedStorageModel;

    @Test
    public void load() {
        given(gameItemLoader.loadChildren(LOCATION, GameItemType.RESERVED_STORAGE, ReservedStorageModel[].class)).willReturn(Arrays.asList(reservedStorageModel));

        given(reservedStorageModel.getId()).willReturn(RESERVED_STORAGE_ID);
        given(reservedStorageModel.getLocation()).willReturn(LOCATION);
        given(reservedStorageModel.getLocationType()).willReturn(LocationType.PLANET.name());
        given(reservedStorageModel.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(reservedStorageModel.getDataId()).willReturn(DATA_ID);
        given(reservedStorageModel.getAmount()).willReturn(AMOUNT);

        ReservedStorages result = underTest.load(LOCATION);

        assertThat(result).hasSize(1);
        ReservedStorage reservedStorage = result.get(0);
        assertThat(reservedStorage.getReservedStorageId()).isEqualTo(RESERVED_STORAGE_ID);
        assertThat(reservedStorage.getLocation()).isEqualTo(LOCATION);
        assertThat(reservedStorage.getLocationType()).isEqualTo(LocationType.PLANET);
        assertThat(reservedStorage.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(reservedStorage.getDataId()).isEqualTo(DATA_ID);
        assertThat(reservedStorage.getAmount()).isEqualTo(AMOUNT);
    }
}