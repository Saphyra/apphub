package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.surface;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
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
public class SurfaceServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();
    private static final Integer PAGE = 246;
    private static final Integer ITEMS_PER_PAGE = 364;

    @Mock
    private SurfaceDao dao;

    @Mock
    private SurfaceModelValidator surfaceModelValidator;

    @InjectMocks
    private SurfaceService underTest;

    @Mock
    private SurfaceModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(dao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.SURFACE);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(surfaceModelValidator).validate(model);
        verify(dao).saveAll(Arrays.asList(model));
    }

    @Test
    public void deleteById() {
        underTest.deleteById(ID);

        verify(dao).deleteById(ID);
    }

    @Test
    void loadPage() {
        given(dao.getPageByGameId(GAME_ID, PAGE, ITEMS_PER_PAGE)).willReturn(List.of(model));

        assertThat(underTest.loadPage(GAME_ID, PAGE, ITEMS_PER_PAGE)).containsExactly(model);
    }
}