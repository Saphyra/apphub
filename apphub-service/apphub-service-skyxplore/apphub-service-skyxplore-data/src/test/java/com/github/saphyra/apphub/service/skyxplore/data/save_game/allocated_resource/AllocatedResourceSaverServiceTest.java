package com.github.saphyra.apphub.service.skyxplore.data.save_game.allocated_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
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
public class AllocatedResourceSaverServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private AllocatedResourceDao allocatedResourceDao;

    @Mock
    private AllocatedResourceModelValidator allocatedResourceModelValidator;

    @InjectMocks
    private AllocatedResourceSaverService underTest;

    @Mock

    private AllocatedResourceModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(allocatedResourceDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.ALLOCATED_RESOURCE);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(allocatedResourceModelValidator).validate(model);
        verify(allocatedResourceDao).saveAll(Arrays.asList(model));
    }
}