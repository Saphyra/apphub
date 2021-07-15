package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.priority;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PriorityServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private PriorityDao priorityDao;

    @Mock
    private PriorityModelValidator priorityModelValidator;

    @InjectMocks
    private PriorityService underTest;

    @Mock
    private PriorityModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(priorityDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.PRIORITY);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(priorityModelValidator).validate(model);
        verify(priorityDao).saveAll(Arrays.asList(model));
    }

    @Test
    public void findById() {
        Throwable ex = catchThrowable(() -> underTest.findById(ID));

        ExceptionValidator.validateReportedException(ex, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR);
    }

    @Test
    public void getByParent() {
        given(priorityDao.getByLocation(ID)).willReturn(Arrays.asList(model));

        List<PriorityModel> result = underTest.getByParent(ID);

        assertThat(result).containsExactly(model);
    }
}