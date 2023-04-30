package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LoadGameItemServiceTest {
    private static final UUID ID = UUID.randomUUID();
    private static final Integer ITEMS_PER_PAGE = 32465;
    private static final Integer PAGE = 346;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameItemService gameItemService;

    @Mock
    private ErrorReporterService errorReporterService;

    private LoadGameItemService underTest;

    @Mock
    private GameItem gameItem;

    @BeforeEach
    public void setUp() {
        underTest = LoadGameItemService.builder()
            .errorReporterService(errorReporterService)
            .gameItemServices(Arrays.asList(gameItemService))
            .itemsPerPage(ITEMS_PER_PAGE)
            .build();
    }

    @Test
    public void loadPageForGameItems_noServiceFound() {
        given(gameItemService.getType()).willReturn(GameItemType.CITIZEN);

        List<? extends GameItem> result = underTest.loadPageForGameItems(ID, PAGE, GameItemType.GAME);

        assertThat(result).isEmpty();
        verify(errorReporterService).report(anyString());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void loadPageForGameItems() {
        given(gameItemService.getType()).willReturn(GameItemType.GAME);
        List gameItems = List.of(gameItem);
        given(gameItemService.loadPage(GAME_ID, PAGE, ITEMS_PER_PAGE)).willReturn(gameItems);

        List result = underTest.loadPageForGameItems(GAME_ID, PAGE, GameItemType.GAME);

        assertThat(result).containsExactly(gameItem);
    }
}