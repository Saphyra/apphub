package com.github.saphyra.apphub.service.modules.service;

import com.github.saphyra.apphub.api.modules.model.response.ModuleResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.modules.ModulesProperties;
import com.github.saphyra.apphub.service.modules.dao.favorite.Favorite;
import com.github.saphyra.apphub.service.modules.dao.favorite.FavoriteService;
import com.github.saphyra.apphub.service.modules.domain.Module;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ModulesQueryServiceTest {
    private static final String CATEGORY_1 = "category-1";
    private static final String CATEGORY_2 = "category-2";
    private static final String CATEGORY_3 = "category-3";
    private static final String MODULE_1 = "module-1";
    private static final String MODULE_2 = "module-2";
    private static final String MODULE_3 = "module-3";
    private static final String MODULE_4 = "module-4";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ROLE_1 = "role-1";
    private static final String ROLE_2 = "role-2";

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private FavoriteService favoriteService;

    @Mock
    private ModulesProperties modulesProperties;

    @InjectMocks
    private ModulesQueryService underTest;

    @Test
    public void getModules() {
        given(accessTokenProvider.get()).willReturn(AccessTokenHeader.builder().userId(USER_ID).accessTokenId(UUID.randomUUID()).roles(Arrays.asList(ROLE_1)).build());

        given(favoriteService.getByUserId(USER_ID)).willReturn(Arrays.asList(
            Favorite.builder().userId(USER_ID).module(MODULE_1).favorite(true).build(),
            Favorite.builder().userId(USER_ID).module(MODULE_2).favorite(false).build()
        ));

        Map<String, List<Module>> modules = new HashMap<>();
        modules.put(CATEGORY_1, Arrays.asList(
            createModule(MODULE_1),
            createModule(MODULE_2)
        ));
        modules.put(CATEGORY_2, Arrays.asList(
            createModule(MODULE_1).toBuilder().mobileAllowed(false).build()
        ));
        modules.put(CATEGORY_3, Arrays.asList(
            createModule(MODULE_3, ROLE_1),
            createModule(MODULE_4, ROLE_2)
        ));
        given(modulesProperties.getModules()).willReturn(modules);

        Map<String, List<ModuleResponse>> result = underTest.getModules(USER_ID, true);

        assertThat(result).containsOnlyKeys(CATEGORY_1, CATEGORY_3);
        assertThat(result.get(CATEGORY_1)).contains(
            ModuleResponse.builder().name(MODULE_1).favorite(true).build(),
            ModuleResponse.builder().name(MODULE_2).favorite(false).build()
        );
        assertThat(result.get(CATEGORY_3)).contains(
            ModuleResponse.builder().name(MODULE_3).favorite(false).build()
        );
    }

    private Module createModule(String module) {
        return createModule(module, null);
    }

    private Module createModule(String moduleName, String role) {
        return Module.builder()
            .name(moduleName)
            .mobileAllowed(true)
            .roles(isNull(role) ? Collections.emptyList() : Arrays.asList(role))
            .build();
    }
}