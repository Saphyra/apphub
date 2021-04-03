package com.github.saphyra.apphub.service.skyxplore.data.save_game.reserved_storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ReservedStorageServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private ReservedStorageDao reservedStorageDao;

    @Mock
    private ReservedStorageModelValidator reservedStorageModelValidator;

    @InjectMocks
    private ReservedStorageService underTest;

    @Mock
    private ReservedStorageModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(reservedStorageDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.RESERVED_STORAGE);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(reservedStorageModelValidator).validate(model);
        verify(reservedStorageDao).saveAll(Arrays.asList(model));
    }
}