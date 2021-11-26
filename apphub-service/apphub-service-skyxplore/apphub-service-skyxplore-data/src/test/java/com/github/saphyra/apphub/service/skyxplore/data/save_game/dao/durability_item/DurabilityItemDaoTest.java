package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.durability_item;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
public class DurabilityItemDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID DURABILITY_ITEM_ID = UUID.randomUUID();
    private static final String DURABILITY_ITEM_ID_STRING = "durability-item-id";
    private static final UUID PARENT = UUID.randomUUID();
    private static final String PARENT_STRING = "parent";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DurabilityItemConverter converter;

    @Mock
    private DurabilityItemRepository repository;

    @InjectMocks
    private DurabilityItemDao underTest;

    @Mock
    private DurabilityItemEntity entity;

    @Mock
    private DurabilityItemModel model;

    @Test
    public void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(DURABILITY_ITEM_ID)).willReturn(DURABILITY_ITEM_ID_STRING);
        given(repository.findById(DURABILITY_ITEM_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<DurabilityItemModel> result = underTest.findById(DURABILITY_ITEM_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByParent() {
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(repository.getByParent(PARENT_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(model));

        List<DurabilityItemModel> result = underTest.getByParent(PARENT);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        given(uuidConverter.convertDomain(DURABILITY_ITEM_ID)).willReturn(DURABILITY_ITEM_ID_STRING);

        underTest.deleteById(DURABILITY_ITEM_ID);

        verify(repository).deleteById(DURABILITY_ITEM_ID_STRING);
    }
}