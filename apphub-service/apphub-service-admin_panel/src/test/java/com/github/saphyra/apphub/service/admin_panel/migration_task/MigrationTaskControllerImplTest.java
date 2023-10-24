package com.github.saphyra.apphub.service.admin_panel.migration_task;

import com.github.saphyra.apphub.api.admin_panel.model.model.MigrationTaskResponse;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.admin_panel.migration_task.dao.MigrationTask;
import com.github.saphyra.apphub.service.admin_panel.migration_task.dao.MigrationTaskDao;
import com.github.saphyra.apphub.service.admin_panel.proxy.EventGatewayProxy;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MigrationTaskControllerImplTest {
    private static final String NAME = "name";
    private static final String EVENT = "event";

    @Mock
    private MigrationTaskDao migrationTaskDao;

    @Mock
    private EventGatewayProxy eventGatewayProxy;

    @InjectMocks
    private MigrationTaskControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Captor
    private ArgumentCaptor<SendEventRequest<?>> argumentCaptor;

    @Test
    void getMigrationTasks() {
        MigrationTask task = MigrationTask.builder()
            .name(NAME)
            .event(EVENT)
            .completed(true)
            .build();
        given(migrationTaskDao.findAll()).willReturn(List.of(task));

        List<MigrationTaskResponse> result = underTest.getMigrationTasks(accessTokenHeader);

        assertThat(result).containsExactly(MigrationTaskResponse.builder().name(NAME).event(EVENT).completed(true).build());
    }

    @Test
    void triggerMigrationTask_notFound() {
        Throwable ex = catchThrowable(() -> underTest.triggerMigrationTask(EVENT, accessTokenHeader));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void triggerMigrationTask_alreadyCompleted() {
        MigrationTask task = MigrationTask.builder()
            .name(NAME)
            .event(EVENT)
            .completed(true)
            .build();
        given(migrationTaskDao.findById(EVENT)).willReturn(Optional.of(task));

        Throwable ex = catchThrowable(() -> underTest.triggerMigrationTask(EVENT, accessTokenHeader));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.GONE, ErrorCode.GENERAL_ERROR);
    }

    @Test
    void triggerMigrationTask() {
        MigrationTask task = MigrationTask.builder()
            .name(NAME)
            .event(EVENT)
            .completed(false)
            .build();
        given(migrationTaskDao.findById(EVENT)).willReturn(Optional.of(task));
        given(migrationTaskDao.findAll()).willReturn(List.of(task));

        List<MigrationTaskResponse> result = underTest.triggerMigrationTask(EVENT, accessTokenHeader);

        then(eventGatewayProxy).should().sendEvent(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getEventName()).isEqualTo(EVENT);

        then(migrationTaskDao).should().save(task);

        assertThat(result).containsExactly(MigrationTaskResponse.builder().name(NAME).event(EVENT).completed(true).build());
    }

    @Test
    void deleteMigrationTask() {
        MigrationTask task = MigrationTask.builder()
            .name(NAME)
            .event(EVENT)
            .completed(false)
            .build();
        given(migrationTaskDao.findAll()).willReturn(List.of(task));

        List<MigrationTaskResponse> result = underTest.deleteMigrationTask(EVENT, accessTokenHeader);

        then(migrationTaskDao).should().deleteById(EVENT);

        assertThat(result).containsExactly(MigrationTaskResponse.builder().name(NAME).event(EVENT).completed(false).build());
    }
}