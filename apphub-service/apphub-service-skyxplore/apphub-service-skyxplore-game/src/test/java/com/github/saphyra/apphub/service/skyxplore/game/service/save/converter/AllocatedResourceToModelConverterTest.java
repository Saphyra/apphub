package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AllocatedResourceToModelConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ALLOCATED_RESOURCE_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int AMOUNT = 324;

    @InjectMocks
    private AllocatedResourceToModelConverter underTest;

    @Mock
    private Game game;

    @Test
    public void convert() {
        given(game.getGameId()).willReturn(GAME_ID);

        AllocatedResource allocatedResource = AllocatedResource.builder()
            .allocatedResourceId(ALLOCATED_RESOURCE_ID)
            .location(LOCATION)
            .locationType(LocationType.PLANET)
            .externalReference(EXTERNAL_REFERENCE)
            .dataId(DATA_ID)
            .amount(AMOUNT)
            .build();

        List<AllocatedResourceModel> result = underTest.convert(Arrays.asList(allocatedResource), game);

        assertThat(result.get(0).getId()).isEqualTo(ALLOCATED_RESOURCE_ID);
        assertThat(result.get(0).getGameId()).isEqualTo(GAME_ID);
        assertThat(result.get(0).getType()).isEqualTo(GameItemType.ALLOCATED_RESOURCE);
        assertThat(result.get(0).getLocation()).isEqualTo(LOCATION);
        assertThat(result.get(0).getLocationType()).isEqualTo(LocationType.PLANET.name());
        assertThat(result.get(0).getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.get(0).getDataId()).isEqualTo(DATA_ID);
        assertThat(result.get(0).getAmount()).isEqualTo(AMOUNT);
    }
}