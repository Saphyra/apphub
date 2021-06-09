package com.github.saphyra.apphub.service.skyxplore.data.game_data;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.data.AbstractDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GameDataControllerImplTest {
    private static final String DATA_ID = "data-id";

    private final AbstractDataService<String, GameDataItem> dataService = new DummyDataService();

    private GameDataControllerImpl underTest;

    @Mock
    private GameDataItem gameDataItem;

    @Before
    public void setUp() {
        given(gameDataItem.getId()).willReturn(DATA_ID);
        dataService.put(DATA_ID, gameDataItem);

        underTest = new GameDataControllerImpl(Arrays.asList(dataService));
    }

    @Test
    public void dataNotFound() {
        Throwable ex = catchThrowable(() -> underTest.getGameData("asd"));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void dataFound() {
        Object result = underTest.getGameData(DATA_ID);

        assertThat(result).isEqualTo(gameDataItem);
    }

    private static class DummyDataService extends AbstractDataService<String, GameDataItem> {

        public DummyDataService() {
            super(null, null);
        }

        @Override
        public void init() {

        }

        @Override
        public void addItem(GameDataItem content, String fileName) {

        }
    }
}