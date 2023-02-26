package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeconstructionServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();

    @Mock
    private DeconstructionDao dao;

    @Mock
    private DeconstructionModelValidator validator;

    @InjectMocks
    private DeconstructionService underTest;

    @Mock
    private DeconstructionModel model;

    @Test
    void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(dao).deleteByGameId(GAME_ID);
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.DECONSTRUCTION);
    }

    @Test
    void save() {
        underTest.save(List.of(model));

        verify(validator).validate(model);
        verify(dao).saveAll(List.of(model));
    }

    @Test
    void findById() {
        given(dao.findById(DECONSTRUCTION_ID)).willReturn(Optional.of(model));

        Optional<DeconstructionModel> result = underTest.findById(DECONSTRUCTION_ID);

        assertThat(result).contains(model);
    }

    @Test
    void getByParent() {
        given(dao.getByExternalReference(EXTERNAL_REFERENCE)).willReturn(List.of(model));

        List<DeconstructionModel> result = underTest.getByParent(EXTERNAL_REFERENCE);

        assertThat(result).containsExactly(model);
    }

    @Test
    void deleteById() {
        underTest.deleteById(DECONSTRUCTION_ID);

        verify(dao).deleteById(DECONSTRUCTION_ID);
    }
}