package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.player;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AllianceCounterTest {
    private static final UUID PLAYER_ID_1 = UUID.randomUUID();
    private static final UUID PLAYER_ID_2 = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();

    @InjectMocks
    private AllianceCounter underTest;

    @Test
    public void getAllianceCount() {
        Map<UUID, UUID> members = CollectionUtils.toMap(
            new BiWrapper<>(PLAYER_ID_1, ALLIANCE_ID),
            new BiWrapper<>(PLAYER_ID_2, null)
        );

        int result = underTest.getAllianceCount(members);

        assertThat(result).isEqualTo(2);
    }
}