package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SystemConnectionModel;
import com.github.saphyra.apphub.service.skyxplore.game.common.LineModelWrapper;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SystemConnectionLoaderTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID SYSTEM_CONNECTION_ID = UUID.randomUUID();

    @Mock
    private GameItemLoader gameItemLoader;

    @Mock
    private LineLoader lineLoader;

    @InjectMocks
    private SystemConnectionLoader underTest;

    @Mock
    private SystemConnectionModel systemConnectionModel;

    @Mock
    private LineModelWrapper lineModelWrapper;

    @Test
    public void load() {
        given(gameItemLoader.loadChildren(GAME_ID, GameItemType.SYSTEM_CONNECTION, SystemConnectionModel[].class)).willReturn(Arrays.asList(systemConnectionModel));
        given(systemConnectionModel.getId()).willReturn(SYSTEM_CONNECTION_ID);
        given(lineLoader.loadOne(SYSTEM_CONNECTION_ID)).willReturn(lineModelWrapper);

        List<SystemConnection> result = underTest.load(GAME_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSystemConnectionId()).isEqualTo(SYSTEM_CONNECTION_ID);
        assertThat(result.get(0).getLine()).isEqualTo(lineModelWrapper);
    }
}