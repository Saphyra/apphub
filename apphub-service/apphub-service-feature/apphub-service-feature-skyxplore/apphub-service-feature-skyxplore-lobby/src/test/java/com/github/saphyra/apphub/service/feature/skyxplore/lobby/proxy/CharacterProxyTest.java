package com.github.saphyra.apphub.service.feature.skyxplore.lobby.proxy;

import com.github.saphyra.apphub.api.feature.skyxplore.data.client.SkyXploreCharacterDataApiClient;
import com.github.saphyra.apphub.api.feature.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
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

    @Mock
    private SkyXploreCharacterDataApiClient characterClient;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private CharacterProxy underTest;

    @Mock
    private AccessToken accessToken;

    @Mock
    private SkyXploreCharacterModel model;

    @Test
    public void getCharacter() {
        given(accessTokenProvider.get()).willReturn(accessToken);
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(characterClient.internalGetCharacterByUserId(USER_ID)).willReturn(model);

        SkyXploreCharacterModel result = underTest.getCharacter();

        assertThat(result).isEqualTo(model);
    }
}