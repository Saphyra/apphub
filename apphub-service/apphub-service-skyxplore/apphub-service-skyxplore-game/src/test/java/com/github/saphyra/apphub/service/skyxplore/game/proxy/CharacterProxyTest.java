package com.github.saphyra.apphub.service.skyxplore.game.proxy;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreCharacterDataApiClient;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
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
    private static final String LOCALE = "locale";
    private static final String NAME = "name";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private LocaleProvider localeProvider;

    @Mock
    private SkyXploreCharacterDataApiClient client;

    @InjectMocks
    private CharacterProxy underTest;

    @Mock
    private SkyXploreCharacterModel characterModel;

    @Test
    void getCharacterName() {
        given(localeProvider.getOrDefault()).willReturn(LOCALE);
        given(characterModel.getName()).willReturn(NAME);
        given(client.internalGetCharacterByUserId(USER_ID, LOCALE)).willReturn(characterModel);

        assertThat(underTest.getCharacterName(USER_ID)).isEqualTo(NAME);
    }
}