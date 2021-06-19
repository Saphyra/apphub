package com.github.saphyra.apphub.service.skyxplore.game.service.map;

import com.github.saphyra.apphub.api.skyxplore.response.game.map.MapResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.game.service.map.query.MapQueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class MapControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private MapQueryService mapQueryService;

    @InjectMocks
    private MapControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private MapResponse mapResponse;

    @Test
    public void getMap() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(mapQueryService.getMap(USER_ID)).willReturn(mapResponse);

        MapResponse result = underTest.getMap(accessTokenHeader);

        assertThat(result).isEqualTo(mapResponse);
    }
}