package com.github.saphyra.apphub.service.feature.skyxplore.game.proxy;

import com.github.saphyra.apphub.api.feature.skyxplore.data.client.SkyXploreCharacterDataApiClient;
import com.github.saphyra.apphub.api.feature.skyxplore.model.SkyXploreCharacterModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CharacterProxyTest {
    private static final String NAME = "name";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private SkyXploreCharacterDataApiClient client;

    @InjectMocks
    private CharacterProxy underTest;

    @Mock
    private SkyXploreCharacterModel characterModel;

    @Test
    void getCharacterName() {
        given(characterModel.getName()).willReturn(NAME);
        given(client.internalGetCharacterByUserId(USER_ID)).willReturn(characterModel);

        assertThat(underTest.getCharacterName(USER_ID)).isEqualTo(NAME);
    }
}