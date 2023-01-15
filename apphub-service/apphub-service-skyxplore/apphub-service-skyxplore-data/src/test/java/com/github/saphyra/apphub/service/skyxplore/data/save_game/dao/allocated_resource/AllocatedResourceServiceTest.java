package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.allocated_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AllocatedResourceServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private AllocatedResourceDao allocatedResourceDao;

    @Mock
    private AllocatedResourceModelValidator allocatedResourceModelValidator;

    @InjectMocks
    private AllocatedResourceService underTest;

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

    @Test
    public void findById() {
        given(allocatedResourceDao.findById(ID)).willReturn(Optional.of(model));

        Optional<AllocatedResourceModel> result = underTest.findById(ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByParent() {
        given(allocatedResourceDao.getByLocation(ID)).willReturn(Arrays.asList(model));

        List<AllocatedResourceModel> result = underTest.getByParent(ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        underTest.deleteById(ID);

        verify(allocatedResourceDao).deleteById(ID);
    }
}