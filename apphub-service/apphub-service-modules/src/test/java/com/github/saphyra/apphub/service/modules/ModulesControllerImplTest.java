package com.github.saphyra.apphub.service.modules;

import com.github.saphyra.apphub.api.modules.model.response.ModuleResponse;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.service.modules.dao.favorite.FavoriteService;
import com.github.saphyra.apphub.service.modules.service.FavoriteUpdateService;
import com.github.saphyra.apphub.service.modules.service.ModulesQueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ModulesControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String CATEGORY = "category";
    private static final String MODULE = "module";

    @Mock
    private FavoriteService favoriteService;

    @Mock
    private FavoriteUpdateService favoriteUpdateService;

    @Mock
    private ModulesQueryService modulesQueryService;

    @InjectMocks
    private ModulesControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    public void deleteAccountEvent() {
        DeleteAccountEvent event = new DeleteAccountEvent(USER_ID);

        SendEventRequest<DeleteAccountEvent> request = SendEventRequest.<DeleteAccountEvent>builder()
            .payload(event)
            .build();
        underTest.deleteAccountEvent(request);

        verify(favoriteService).deleteByUserId(USER_ID);
    }

    @Test
    public void getModules() {
        Map<String, List<ModuleResponse>> responseMap = new HashMap<>();
        responseMap.put(CATEGORY, Arrays.asList(new ModuleResponse()));
        given(modulesQueryService.getModules(USER_ID)).willReturn(responseMap);
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        Map<String, List<ModuleResponse>> result = underTest.getModules(accessTokenHeader);

        assertThat(result).isEqualTo(responseMap);
    }

    @Test
    public void setFavorite() {
        Map<String, List<ModuleResponse>> responseMap = new HashMap<>();
        responseMap.put(CATEGORY, Arrays.asList(new ModuleResponse()));
        given(modulesQueryService.getModules(USER_ID)).willReturn(responseMap);
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        Map<String, List<ModuleResponse>> result = underTest.setFavorite(accessTokenHeader, MODULE, new OneParamRequest<>(true));

        verify(favoriteUpdateService).updateFavorite(USER_ID, MODULE, true);
        assertThat(result).isEqualTo(responseMap);
    }
}