package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class StarSystemWriteBufferTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();

    @Mock
    private StarSystemRepository repository;

    @Mock
    private StarSystemConverter converter;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private ErrorReporterService errorReporterService;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @InjectMocks
    private StarSystemWriteBuffer underTest;

    @Mock
    private StarSystem domain;

    @Mock
    private StarSystemEntity entity;

    @Test
    void synchronize() {
        underTest.add(STAR_SYSTEM_ID, domain);
        given(converter.convertDomain(any(Collection.class))).willReturn(List.of(entity));

        underTest.synchronize();

        then(repository).should().saveAll(List.of(entity));
    }

    @Test
    void synchronize_error() {
        underTest.add(STAR_SYSTEM_ID, domain);
        underTest.add(UUID.randomUUID(), domain);
        given(converter.convertDomain(any(Collection.class))).willThrow(new RuntimeException());
        given(converter.convertDomain(domain)).willReturn(entity);
        given(repository.save(any()))
            .willThrow(new RuntimeException())
            .willReturn(entity);
        given(executorServiceBean.execute(any(Runnable.class))).willAnswer(invocationOnMock -> {
            invocationOnMock.getArgument(0, Runnable.class).run();
            return null;
        });

        underTest.synchronize();

        then(errorReporterService).should(times(2)).report(anyString(), any());
        then(repository).should(times(2)).save(entity);
    }
}