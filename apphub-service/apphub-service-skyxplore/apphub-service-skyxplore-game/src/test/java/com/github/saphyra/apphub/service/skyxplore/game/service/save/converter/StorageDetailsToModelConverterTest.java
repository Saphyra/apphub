package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResources;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageSettings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StorageDetailsToModelConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private AllocatedResourceToModelConverter allocatedResourceConverter;

    @Mock
    private ReservedStorageToModelConverter reservedStorageConverter;

    @Mock
    private StoredResourceToModelConverter storedResourceConverter;

    @Mock
    private StorageSettingToModelConverter storageSettingConverter;

    @InjectMocks
    private StorageDetailsToModelConverter underTest;

    @Mock
    private Game game;

    @Mock
    private AllocatedResources allocatedResources;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private StoredResources storedResources;

    @Mock
    private StorageSettings storageSettings;

    @Mock
    private AllocatedResourceModel allocatedResourceModel;

    @Mock
    private ReservedStorageModel reservedStorageModel;

    @Mock
    private StoredResourceModel storedResourceModel;

    @Mock
    private StorageSettingModel storageSettingModel;

    @Test
    public void convertDeep() {
        given(allocatedResourceConverter.convert(allocatedResources, game)).willReturn(Arrays.asList(allocatedResourceModel));
        given(reservedStorageConverter.convert(reservedStorages, game)).willReturn(Arrays.asList(reservedStorageModel));
        given(storedResourceConverter.convert(storedResources, GAME_ID)).willReturn(Arrays.asList(storedResourceModel));
        given(storageSettingConverter.convert(storageSettings, game)).willReturn(Arrays.asList(storageSettingModel));

        StorageDetails storageDetails = StorageDetails.builder()
            .allocatedResources(allocatedResources)
            .reservedStorages(reservedStorages)
            .storedResources(storedResources)
            .storageSettings(storageSettings)
            .build();

        given(game.getGameId()).willReturn(GAME_ID);

        List<GameItem> result = underTest.convertDeep(storageDetails, game);

        assertThat(result).containsExactlyInAnyOrder(allocatedResourceModel, reservedStorageModel, storageSettingModel, storedResourceModel);
    }
}