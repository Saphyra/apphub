package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.durability;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DurabilityDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID DURABILITY_ITEM_ID = UUID.randomUUID();
    private static final String DURABILITY_ITEM_ID_STRING = "durability-item-id";
    private static final int PAGE = 235;
    private static final int ITEMS_PER_PAGE = 457;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DurabilityItemConverter converter;

    @Mock
    private DurabilityRepository repository;

    @InjectMocks
    private DurabilityDao underTest;

    @Mock
    private DurabilityEntity entity;

    @Mock
    private DurabilityModel model;

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

        Optional<DurabilityModel> result = underTest.findById(DURABILITY_ITEM_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void deleteById() {
        given(uuidConverter.convertDomain(DURABILITY_ITEM_ID)).willReturn(DURABILITY_ITEM_ID_STRING);

        underTest.deleteById(DURABILITY_ITEM_ID);

        verify(repository).deleteById(DURABILITY_ITEM_ID_STRING);
    }

    @Test
    void getPageByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.getByGameId(GAME_ID_STRING, PageRequest.of(PAGE, ITEMS_PER_PAGE))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(model));

        assertThat(underTest.getPageByGameId(GAME_ID, PAGE, ITEMS_PER_PAGE)).containsExactly(model);
    }
}