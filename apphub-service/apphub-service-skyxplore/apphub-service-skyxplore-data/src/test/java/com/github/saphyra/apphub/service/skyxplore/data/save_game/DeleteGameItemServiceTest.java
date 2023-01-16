package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeleteGameItemServiceTest {
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private GameItemService gameItemService;

    private DeleteGameItemService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new DeleteGameItemService(Arrays.asList(gameItemService));
    }

    @Test
    public void serviceNotFound() {
        Throwable ex = catchThrowable(() -> underTest.deleteItem(ID, GameItemType.PLAYER));

        ExceptionValidator.validateReportedException(ex, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR);
    }

    @Test
    public void deleteItem() {
        given(gameItemService.getType()).willReturn(GameItemType.PLAYER);

        underTest.deleteItem(ID, GameItemType.PLAYER);

        verify(gameItemService).deleteById(ID);
    }
}