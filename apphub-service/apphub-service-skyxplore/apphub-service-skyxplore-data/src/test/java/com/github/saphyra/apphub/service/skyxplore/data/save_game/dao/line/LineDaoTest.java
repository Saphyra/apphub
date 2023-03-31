package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.line;

import com.github.saphyra.apphub.api.skyxplore.model.game.LineModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LineDaoTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_ID_STRING = "game-id";
    private static final UUID LINE_ID = UUID.randomUUID();
    private static final String LINE_ID_STRING = "line-id";
    private static final UUID REFERENCE_ID = UUID.randomUUID();
    private static final String REFERENCE_ID_STRING = "reference-id";
    private static final int PAGE = 3246;
    private static final int ITEMS_PER_PAGE = 376;

    @Mock
    private LineRepository repository;

    @Mock
    private LineConverter converter;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private LineDao underTest;

    @Mock
    private LineEntity entity;

    @Mock
    private LineModel model;

    @Test
    public void deleteByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);

        underTest.deleteByGameId(GAME_ID);

        verify(repository).deleteByGameId(GAME_ID_STRING);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(LINE_ID)).willReturn(LINE_ID_STRING);
        given(repository.findById(LINE_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(model));

        Optional<LineModel> result = underTest.findById(LINE_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByReferenceId() {
        given(uuidConverter.convertDomain(REFERENCE_ID)).willReturn(REFERENCE_ID_STRING);
        given(repository.getByReferenceId(REFERENCE_ID_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(model));

        List<LineModel> result = underTest.getByReferenceId(REFERENCE_ID);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        given(uuidConverter.convertDomain(LINE_ID)).willReturn(LINE_ID_STRING);
        given(repository.existsById(LINE_ID_STRING)).willReturn(true);

        underTest.deleteById(LINE_ID);

        verify(repository).deleteById(LINE_ID_STRING);
    }

    @Test
    void getPageByGameId() {
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(repository.getByGameId(GAME_ID_STRING, PageRequest.of(PAGE, ITEMS_PER_PAGE))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(model));

        assertThat(underTest.getPageByGameId(GAME_ID, PAGE, ITEMS_PER_PAGE)).containsExactly(model);
    }
}