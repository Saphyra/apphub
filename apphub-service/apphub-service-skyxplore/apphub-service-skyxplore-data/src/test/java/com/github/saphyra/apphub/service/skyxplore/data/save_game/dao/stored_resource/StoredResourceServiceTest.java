package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.stored_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StoredResourceServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private StoredResourceDao storedResourceDao;

    @Mock
    private StoredResourceModelValidator storedResourceModelValidator;

    @InjectMocks
    private StoredResourceService underTest;

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

    @Test
    public void findById() {
        given(storedResourceDao.findById(ID)).willReturn(Optional.of(model));

        Optional<StoredResourceModel> result = underTest.findById(ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByParent() {
        given(storedResourceDao.getByLocation(ID)).willReturn(Arrays.asList(model));

        List<StoredResourceModel> result = underTest.getByParent(ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        underTest.deleteById(ID);

        verify(storedResourceDao).deleteById(ID);
    }
}