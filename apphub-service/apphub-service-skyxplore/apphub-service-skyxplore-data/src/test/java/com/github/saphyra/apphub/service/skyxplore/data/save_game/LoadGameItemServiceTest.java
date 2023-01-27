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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LoadGameItemServiceTest {
    private static final UUID ID = UUID.randomUUID();

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
            .build();
    }

    @Test
    public void loadGameItem_noServiceFound() {
        given(gameItemService.getType()).willReturn(GameItemType.CITIZEN);

        GameItem result = underTest.loadGameItem(ID, GameItemType.GAME);

        assertThat(result).isNull();
        verify(errorReporterService).report(anyString());
    }

    @Test
    public void loadGameItem_itemNotFound() {
        given(gameItemService.getType()).willReturn(GameItemType.GAME);
        given(gameItemService.findById(ID)).willReturn(Optional.empty());

        GameItem result = underTest.loadGameItem(ID, GameItemType.GAME);

        assertThat(result).isNull();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void loadGameItem() {
        given(gameItemService.getType()).willReturn(GameItemType.GAME);
        Optional gameItemOptional = Optional.of(gameItem);
        given(gameItemService.findById(ID)).willReturn(gameItemOptional);

        GameItem result = underTest.loadGameItem(ID, GameItemType.GAME);

        assertThat(result).isEqualTo(gameItem);
    }

    @Test
    public void loadChildrenOfGameItem_noServiceFound() {
        given(gameItemService.getType()).willReturn(GameItemType.CITIZEN);

        List<? extends GameItem> result = underTest.loadChildrenOfGameItem(ID, GameItemType.GAME);

        assertThat(result).isEmpty();
        verify(errorReporterService).report(anyString());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void loadChildrenOfGameItem() {
        given(gameItemService.getType()).willReturn(GameItemType.GAME);
        List list = Arrays.asList(gameItem);
        given(gameItemService.getByParent(ID)).willReturn(list);

        List result = underTest.loadChildrenOfGameItem(ID, GameItemType.GAME);

        assertThat(result).containsExactly(gameItem);
    }
}