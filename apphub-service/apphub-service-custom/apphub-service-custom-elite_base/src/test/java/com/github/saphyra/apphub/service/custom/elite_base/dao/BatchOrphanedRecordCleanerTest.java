package com.github.saphyra.apphub.service.custom.elite_base.dao;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.custom.elite_base.common.OrphanedRecordCleanerProperties;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BatchOrphanedRecordCleanerTest {
    private static final Integer BATCH_SIZE = 3;

    @Mock
    private EliteBaseProperties eliteBaseProperties;

    @Mock
    private ErrorReporterService errorReporterService;

    @Mock
    private Supplier<List<String>> idSupplier;

    @Mock
    private Consumer<List<String>> idConsumer;

    private TestBatchOrphanedRecordCleaner underTest;

    @Mock
    private OrphanedRecordCleanerProperties orphanedRecordCleanerProperties;

    @BeforeEach
    void setUp() {
        underTest = TestBatchOrphanedRecordCleaner.builder()
            .errorReporterService(errorReporterService)
            .eliteBaseProperties(eliteBaseProperties)
            .idSupplier(idSupplier)
            .idConsumer(idConsumer)
            .build();
    }

    @Test
    void doCleanup() {
        given(eliteBaseProperties.getOrphanedRecordCleaner()).willReturn(orphanedRecordCleanerProperties);
        given(orphanedRecordCleanerProperties.getBatchSize()).willReturn(BATCH_SIZE);
        List<String> firstBatch = Stream.generate(() -> "id").limit(BATCH_SIZE).collect(Collectors.toList());
        List<String> secondBatch = Stream.generate(() -> "id").limit(BATCH_SIZE - 1).collect(Collectors.toList());
        given(idSupplier.get())
            .willReturn(firstBatch)
            .willReturn(secondBatch);

        assertThat(underTest.doCleanup()).isEqualTo(5);

        then(idConsumer).should().accept(firstBatch);
        then(idConsumer).should().accept(secondBatch);
        then(idConsumer).shouldHaveNoMoreInteractions();
    }

    private static class TestBatchOrphanedRecordCleaner extends BatchOrphanedRecordCleaner {
        private final Supplier<List<String>> idSupplier;
        private final Consumer<List<String>> idConsumer;

        @Builder
        protected TestBatchOrphanedRecordCleaner(ErrorReporterService errorReporterService, EliteBaseProperties eliteBaseProperties, Supplier<List<String>> idSupplier, Consumer<List<String>> idConsumer) {
            super(errorReporterService, eliteBaseProperties);
            this.idSupplier = idSupplier;
            this.idConsumer = idConsumer;
        }

        @Override
        public Orphanage getOrphanage() {
            return null;
        }

        @Override
        public List<Orphanage> getPreconditions() {
            return null;
        }

        @Override
        protected void delete(List<String> idsToDelete) {
            idConsumer.accept(idsToDelete);
        }

        @Override
        protected List<String> fetchIds() {
            return idSupplier.get();
        }
    }
}