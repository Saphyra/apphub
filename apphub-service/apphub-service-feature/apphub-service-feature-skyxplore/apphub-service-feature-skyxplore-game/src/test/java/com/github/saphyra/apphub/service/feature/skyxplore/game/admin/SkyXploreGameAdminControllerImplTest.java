package com.github.saphyra.apphub.service.feature.skyxplore.game.admin;

import com.github.saphyra.apphub.api.feature.skyxplore.admin.SkyXploreGameDataDetails;
import com.github.saphyra.apphub.api.feature.skyxplore.admin.SkyXploreGameDataEntry;
import com.github.saphyra.apphub.api.feature.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.service.feature.skyxplore.game.admin.service.AdminQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SkyXploreGameAdminControllerImplTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ITEM_ID = UUID.randomUUID();

    @Mock
    private AdminQueryService adminQueryService;

    private SkyXploreGameAdminControllerImpl underTest;

    @Mock
    private SkyXploreGameDataEntry entry;

    @Mock
    private SkyXploreGameDataDetails details;

    @Mock
    private AccessToken accessToken;

    @BeforeEach
    void setUp() {
        given(adminQueryService.getType()).willReturn(GameItemType.CONSTRUCTION);

        underTest = new SkyXploreGameAdminControllerImpl(List.of(adminQueryService));
    }

    @Test
    void getByType() {
        given(adminQueryService.getAll(GAME_ID)).willReturn(List.of(entry));

        assertThat(underTest.getByType(GameItemType.CONSTRUCTION, GAME_ID, accessToken)).containsExactly(entry);
    }

    @Test
    void getItem() {
        given(adminQueryService.findById(GAME_ID, ITEM_ID)).willReturn(details);

        assertThat(underTest.getItem(GAME_ID, GameItemType.CONSTRUCTION, ITEM_ID, accessToken)).isEqualTo(details);
    }
}