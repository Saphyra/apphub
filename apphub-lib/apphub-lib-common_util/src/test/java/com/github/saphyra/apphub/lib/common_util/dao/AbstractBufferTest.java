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
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AbstractBufferTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final String ITEM = "item";

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private Consumer<Collection<String>> doSynchronize;

    @Mock
    private Consumer<Collection<String>> afterSynchronize;

    private final List<String> buffer = new ArrayList<>();

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
        buffer.add(ITEM);

        assertThat(underTest.getSize()).isEqualTo(1);
    }

    @Test
    void synchronize_emptyBuffer() {
        underTest.synchronize();

        then(writeLock).shouldHaveNoInteractions();
    }

    @Test
    void synchronize() {
        buffer.add(ITEM);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);

        underTest.synchronize();

        then(writeLock).should().lock();
        then(doSynchronize).should().accept(List.of(ITEM));
        then(writeLock).should().unlock();
        then(afterSynchronize).should().accept(List.of(ITEM));

        assertThat(underTest.getLastSynchronized()).isEqualTo(CURRENT_TIME);
        assertThat(buffer).isEmpty();
    }

    private static class AbstractBufferImpl extends AbstractBuffer<String> {
        private final Consumer<Collection<String>> doSynchronize;
        private final Consumer<Collection<String>> afterSynchronize;
        private final Collection<String> buffer;
        private final Lock writeLock;

        @Builder
        protected AbstractBufferImpl(DateTimeUtil dateTimeUtil, Consumer<Collection<String>> doSynchronize, Consumer<Collection<String>> afterSynchronize, Collection<String> buffer, Lock writeLock) {
            super(dateTimeUtil);
            this.doSynchronize = doSynchronize;
            this.afterSynchronize = afterSynchronize;
            this.buffer = buffer;
            this.writeLock = writeLock;
        }

        @Override
        protected void afterSynchronize(Collection<String> bufferCopy) {
            afterSynchronize.accept(bufferCopy);
        }

        @Override
        protected Collection<String> getBuffer() {
            return buffer;
        }

        @Override
        protected Lock getWriteLock() {
            return writeLock;
        }

        @Override
        protected void doSynchronize(Collection<String> bufferCopy) {
            doSynchronize.accept(bufferCopy);
        }

        @Override
        public int getOrder() {
            return 0;
        }
    }
}