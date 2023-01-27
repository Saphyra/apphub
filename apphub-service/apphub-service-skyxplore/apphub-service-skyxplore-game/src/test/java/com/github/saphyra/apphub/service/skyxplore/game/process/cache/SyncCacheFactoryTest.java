package com.github.saphyra.apphub.service.skyxplore.game.process.cache;

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

    @Mock
    private MessageCacheFactory messageCacheFactory;

    @InjectMocks
    private SyncCacheFactory underTest;

    @Mock
    private GameItemCache gameItemCache;

    @Mock
    private MessageCache messageCache;

    @Test
    public void create() {
        given(gameItemCacheFactory.create()).willReturn(gameItemCache);
        given(messageCacheFactory.create()).willReturn(messageCache);

        assertThat(underTest.create()).isNotNull();
    }
}