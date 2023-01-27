package com.github.saphyra.apphub.service.skyxplore.lobby.proxy;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreCharacterDataApiClient;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
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
public class CharacterProxyTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOCALE = "locale";

    @Mock
    private LocaleProvider localeProvider;

    @Mock
    private SkyXploreCharacterDataApiClient characterClient;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private CharacterProxy underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private SkyXploreCharacterModel model;

    @Test
    public void getCharacter() {
        given(accessTokenProvider.get()).willReturn(accessTokenHeader);
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
        given(characterClient.internalGetCharacterByUserId(USER_ID, LOCALE)).willReturn(model);

        SkyXploreCharacterModel result = underTest.getCharacter();

        assertThat(result).isEqualTo(model);
    }
}