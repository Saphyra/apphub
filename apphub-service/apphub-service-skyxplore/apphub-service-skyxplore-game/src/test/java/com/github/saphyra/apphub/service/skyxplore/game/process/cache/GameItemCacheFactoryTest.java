package com.github.saphyra.apphub.service.skyxplore.game.process.cache;

import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GameItemCacheFactoryTest {
    @SuppressWarnings("unused")
    @Mock
    private GameDataProxy gameDataProxy;

    @InjectMocks
    private GameItemCacheFactory underTest;

    @Test
    public void create() {
        assertThat(underTest.create()).isNotNull();
    }
}