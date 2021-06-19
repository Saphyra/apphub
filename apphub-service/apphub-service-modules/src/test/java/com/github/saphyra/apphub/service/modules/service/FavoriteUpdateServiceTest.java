package com.github.saphyra.apphub.service.modules.service;

import com.github.saphyra.apphub.service.modules.ModulesProperties;
import com.github.saphyra.apphub.service.modules.dao.favorite.Favorite;
import com.github.saphyra.apphub.service.modules.dao.favorite.FavoriteService;
import com.github.saphyra.apphub.service.modules.domain.Module;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FavoriteUpdateServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String MODULE = "module";

    @Mock
    private FavoriteService favoriteService;

    @Mock
    private ModulesProperties modulesProperties;

    @InjectMocks
    private FavoriteUpdateService underTest;

    @Mock
    private Favorite favorite;

    @Test
    public void invalidModule() {
        given(modulesProperties.getModules()).willReturn(Collections.emptyMap());

        Throwable ex = catchThrowable(() -> underTest.updateFavorite(USER_ID, MODULE, true));

        ExceptionValidator.validateInvalidParam(ex, "module", "does not exist");
    }

    @Test
    public void nullValue() {
        Map<String, List<Module>> moduleMap = new HashMap<>();
        moduleMap.put("asd", Arrays.asList(Module.builder().name(MODULE).build()));
        given(modulesProperties.getModules()).willReturn(moduleMap);

        Throwable ex = catchThrowable(() -> underTest.updateFavorite(USER_ID, MODULE, null));

        ExceptionValidator.validateInvalidParam(ex, "value", "must not be null");
    }

    @Test
    public void setAsFavorite() {
        Map<String, List<Module>> moduleMap = new HashMap<>();
        moduleMap.put("asd", Arrays.asList(Module.builder().name(MODULE).build()));
        given(modulesProperties.getModules()).willReturn(moduleMap);

        given(favoriteService.getOrDefault(USER_ID, MODULE)).willReturn(favorite);

        underTest.updateFavorite(USER_ID, MODULE, true);

        verify(favorite).setFavorite(true);
        verify(favoriteService).save(favorite);
    }
}