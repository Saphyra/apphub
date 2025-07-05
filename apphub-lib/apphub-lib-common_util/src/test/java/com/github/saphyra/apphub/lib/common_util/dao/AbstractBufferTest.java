package com.github.saphyra.apphub.lib.common_util.dao;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AbstractBufferTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private Runnable doSynchronize;

    @Mock
    private Runnable afterSynchronize;

    private final List<Object> buffer = new ArrayList<>();

    @Mock
    private Lock writeLock;

    private AbstractBufferImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = AbstractBufferImpl.builder()
            .dateTimeUtil(dateTimeUtil)
            .doSynchronize(doSynchronize)
            .buffer(buffer)
            .writeLock(writeLock)
            .afterSynchronize(afterSynchronize)
            .build();
    }

    @Test
    void getSize() {
        buffer.add(new Object());

        assertThat(underTest.getSize()).isEqualTo(1);
    }

    @Test
    void synchronize_emptyBuffer() {
        underTest.synchronize();

        then(writeLock).shouldHaveNoInteractions();
    }

    @Test
    void synchronize() {
        buffer.add(new Object());
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);

        underTest.synchronize();

        then(writeLock).should().lock();
        then(doSynchronize).should().run();
        then(writeLock).should().unlock();
        then(afterSynchronize).should().run();

        assertThat(underTest.getLastSynchronized()).isEqualTo(CURRENT_TIME);
        assertThat(buffer).isEmpty();
    }

    private static class AbstractBufferImpl extends AbstractBuffer {
        private final Runnable doSynchronize;
        private final Runnable afterSynchronize;
        private final Collection<?> buffer;
        private final Lock writeLock;

        @Builder
        protected AbstractBufferImpl(DateTimeUtil dateTimeUtil, Runnable doSynchronize, Runnable afterSynchronize, Collection<?> buffer, Lock writeLock) {
            super(dateTimeUtil);
            this.doSynchronize = doSynchronize;
            this.afterSynchronize = afterSynchronize;
            this.buffer = buffer;
            this.writeLock = writeLock;
        }

        @Override
        protected void doSynchronize() {
            doSynchronize.run();
        }

        @Override
        protected void afterSynchronize() {
            afterSynchronize.run();
        }

        @Override
        protected Collection<?> getBuffer() {
            return buffer;
        }

        @Override
        protected Lock getWriteLock() {
            return writeLock;
        }
    }
}