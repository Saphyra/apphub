package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
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
class DeconstructionDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final String DECONSTRUCTION_ID_STRING = "deconstruction-id";
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";
    private static final int PAGE = 2345;
    private static final int ITEMS_PER_PAGE = 356;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private DeconstructionConverter converter;

    @Mock
    private DeconstructionRepository repository;

    @InjectMocks
    private DeconstructionDao underTest;

    @Mock
    private DeconstructionEntity entity;

    @Mock
    private DeconstructionModel model;

    @Test
    void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    void findById() {
        given(uuidConverter.convertDomain(DECONSTRUCTION_ID)).willReturn(DECONSTRUCTION_ID_STRING);
        given(repository.findById(DECONSTRUCTION_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<DeconstructionModel> result = underTest.findById(DECONSTRUCTION_ID);

        assertThat(result).contains(model);
    }

    @Test
    void getByExternalReference() {
        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);
        given(repository.getByExternalReference(EXTERNAL_REFERENCE_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(model));

        List<DeconstructionModel> result = underTest.getByExternalReference(EXTERNAL_REFERENCE);

        assertThat(result).containsExactly(model);
    }

    @Test
    void deleteById() {
        given(uuidConverter.convertDomain(DECONSTRUCTION_ID)).willReturn(DECONSTRUCTION_ID_STRING);
        given(repository.existsById(DECONSTRUCTION_ID_STRING)).willReturn(true);

        underTest.deleteById(DECONSTRUCTION_ID);

        verify(repository).deleteById(DECONSTRUCTION_ID_STRING);
    }

    @Test
    void getPageByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.getByGameId(GAME_ID_STRING, PageRequest.of(PAGE, ITEMS_PER_PAGE))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(model));

        assertThat(underTest.getPageByGameId(GAME_ID, PAGE, ITEMS_PER_PAGE)).containsExactly(model);
    }
}