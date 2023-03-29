package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ReservedStorageToModelConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int AMOUNT = 3215;
    private static final UUID LOCATION = UUID.randomUUID();

    @InjectMocks
    private ReservedStorageToModelConverter underTest;

    @Test
    public void convert() {
        ReservedStorage reservedStorage = ReservedStorage.builder()
            .reservedStorageId(RESERVED_STORAGE_ID)
            .location(LOCATION)
            .externalReference(EXTERNAL_REFERENCE)
            .dataId(DATA_ID)
            .amount(AMOUNT)
            .build();

        List<ReservedStorageModel> result = underTest.convert(GAME_ID, Arrays.asList(reservedStorage));

        assertThat(result.get(0).getId()).isEqualTo(RESERVED_STORAGE_ID);
        assertThat(result.get(0).getGameId()).isEqualTo(GAME_ID);
        assertThat(result.get(0).getType()).isEqualTo(GameItemType.RESERVED_STORAGE);
        assertThat(result.get(0).getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.get(0).getDataId()).isEqualTo(DATA_ID);
        assertThat(result.get(0).getAmount()).isEqualTo(AMOUNT);
        assertThat(result.get(0).getLocation()).isEqualTo(LOCATION);
    }
}