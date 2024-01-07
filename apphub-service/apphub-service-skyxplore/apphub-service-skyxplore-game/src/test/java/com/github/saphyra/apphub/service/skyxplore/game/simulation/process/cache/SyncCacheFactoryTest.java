package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SyncCacheFactoryTest {
    @Mock
    private GameItemCacheFactory gameItemCacheFactory;


    @InjectMocks
    private SyncCacheFactory underTest;

    @Mock
    private Game game;

    @Mock
    private GameItemCache gameItemCache;


    @Test
    public void create() {
        given(gameItemCacheFactory.create()).willReturn(gameItemCache);

        assertThat(underTest.create()).isNotNull();
    }
}