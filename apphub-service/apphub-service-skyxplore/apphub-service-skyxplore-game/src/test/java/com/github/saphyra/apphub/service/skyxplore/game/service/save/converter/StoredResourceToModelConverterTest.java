package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class StoredResourceToModelConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID STORED_RESOURCE_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int AMOUNT = 4322;

    @InjectMocks
    private StoredResourceToModelConverter underTest;

    @Test
    public void convert() {
        StoredResource storedResource = StoredResource.builder()
            .storedResourceId(STORED_RESOURCE_ID)
            .location(LOCATION)
            .locationType(LocationType.PLANET)
            .dataId(DATA_ID)
            .amount(AMOUNT)
            .build();

        List<StoredResourceModel> result = underTest.convert(CollectionUtils.toMap("", storedResource), GAME_ID);

        assertThat(result.get(0).getId()).isEqualTo(STORED_RESOURCE_ID);
        assertThat(result.get(0).getGameId()).isEqualTo(GAME_ID);
        assertThat(result.get(0).getType()).isEqualTo(GameItemType.STORED_RESOURCE);
        assertThat(result.get(0).getLocation()).isEqualTo(LOCATION);
        assertThat(result.get(0).getLocationType()).isEqualTo(LocationType.PLANET.name());
        assertThat(result.get(0).getDataId()).isEqualTo(DATA_ID);
        assertThat(result.get(0).getAmount()).isEqualTo(AMOUNT);
    }
}