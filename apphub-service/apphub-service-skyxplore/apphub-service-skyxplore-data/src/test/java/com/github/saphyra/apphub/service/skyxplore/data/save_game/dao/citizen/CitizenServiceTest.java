package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CitizenServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final Integer PAGE = 2345;
    private static final Integer ITEMS_PER_PAGE = 346;

    @Mock
    private CitizenDao dao;

    @Mock
    private CitizenModelValidator citizenModelValidator;

    @InjectMocks
    private CitizenService underTest;

    @Mock
    private CitizenModel model;


    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(dao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.CITIZEN);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(citizenModelValidator).validate(model);
        verify(dao).saveAll(Arrays.asList(model));
    }

    @Test
    public void deleteById() {
        underTest.deleteById(CITIZEN_ID);

        verify(dao).deleteById(CITIZEN_ID);
    }

    @Test
    void loadPage() {
        given(dao.getPageByGameId(GAME_ID, PAGE, ITEMS_PER_PAGE)).willReturn(List.of(model));

        assertThat(underTest.loadPage(GAME_ID, PAGE, ITEMS_PER_PAGE)).containsExactly(model);
    }
}