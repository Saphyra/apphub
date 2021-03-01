package com.github.saphyra.apphub.service.skyxplore.data.save_game.stored_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
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
public class StoredResourceSaverServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private StoredResourceDao storedResourceDao;

    @Mock
    private StoredResourceModelValidator storedResourceModelValidator;

    @InjectMocks
    private StoredResourceSaverService underTest;

    @Mock
    private StoredResourceModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(storedResourceDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.STORED_RESOURCE);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(storedResourceModelValidator).validate(model);
        verify(storedResourceDao).saveAll(Arrays.asList(model));
    }
}