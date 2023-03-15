package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Player;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AllianceLoaderTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String ALLIANCE_NAME = "alliance-name";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private AllianceLoader underTest;

    @Mock
    private Player player1;

    @Mock
    private Player player2;

    @Mock
    private AllianceModel allianceModel;

    @Test
    public void load() {
        given(gameItemLoader.loadChildren(GAME_ID, GameItemType.ALLIANCE, AllianceModel[].class)).willReturn(Arrays.asList(allianceModel));

        given(allianceModel.getId()).willReturn(ALLIANCE_ID);
        given(allianceModel.getName()).willReturn(ALLIANCE_NAME);
        given(player1.getAllianceId()).willReturn(ALLIANCE_ID);
        given(player1.getUserId()).willReturn(USER_ID);

        Map<UUID, Alliance> result = underTest.load(GAME_ID, CollectionUtils.toMap(new BiWrapper<>(UUID.randomUUID(), player1), new BiWrapper<>(UUID.randomUUID(), player2)));

        assertThat(result).hasSize(1);
        Alliance alliance = result.get(ALLIANCE_ID);
        assertThat(alliance.getAllianceId()).isEqualTo(ALLIANCE_ID);
        assertThat(alliance.getAllianceName()).isEqualTo(ALLIANCE_NAME);
        assertThat(alliance.getMembers()).containsEntry(USER_ID, player1);
    }
}