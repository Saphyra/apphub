package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
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
public class ConstructionDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID ID = UUID.randomUUID();
    private static final String ID_STRING = "id";
    private static final int PAGE = 235;
    private static final int ITEMS_PER_PAGE = 3456;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ConstructionRepository repository;

    @Mock
    private ConstructionConverter converter;

    @InjectMocks
    private ConstructionDao underTest;

    @Mock
    private ConstructionEntity entity;

    @Mock
    private ConstructionModel model;

    @Test
    public void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(repository.findById(ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<ConstructionModel> result = underTest.findById(ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByExternalReference() {
        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(repository.getByExternalReference(ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(model));

        List<ConstructionModel> result = underTest.getByExternalReference(ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(repository.existsById(ID_STRING)).willReturn(true);

        underTest.deleteById(ID);

        verify(repository).deleteById(ID_STRING);
    }

    @Test
    void getPageByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.getByGameId(GAME_ID_STRING, PageRequest.of(PAGE, ITEMS_PER_PAGE))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(model));

        assertThat(underTest.getPageByGameId(GAME_ID, PAGE, ITEMS_PER_PAGE)).containsExactly(model);
    }
}