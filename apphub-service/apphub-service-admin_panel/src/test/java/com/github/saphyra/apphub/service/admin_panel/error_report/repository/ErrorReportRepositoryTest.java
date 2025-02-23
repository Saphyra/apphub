package com.github.saphyra.apphub.service.admin_panel.error_report.repository;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class ErrorReportRepositoryTest {
    private static final String ERROR_REPORT_ID_1 = "error-report-id-1";
    private static final String ERROR_REPORT_ID_2 = "error-report-id-2";
    private static final String STATUS_1 = "status-1";
    private static final String STATUS_2 = "status-2";

    @Autowired
    private ErrorReportRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    public void deleteByStatus() {
        ErrorReportEntity entity1 = ErrorReportEntity.builder()
            .id(ERROR_REPORT_ID_1)
            .status(STATUS_1)
            .build();
        ErrorReportEntity entity2 = ErrorReportEntity.builder()
            .id(ERROR_REPORT_ID_2)
            .status(STATUS_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByStatus(STATUS_2);

        assertThat(underTest.findAll()).containsExactly(entity1);
    }

    @Test
    @Transactional
    void deleteByStatusNotIn(){
        ErrorReportEntity entity1 = ErrorReportEntity.builder()
            .id(ERROR_REPORT_ID_1)
            .status(STATUS_1)
            .build();
        ErrorReportEntity entity2 = ErrorReportEntity.builder()
            .id(ERROR_REPORT_ID_2)
            .status(STATUS_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByStatusNotIn(List.of(STATUS_1));

        assertThat(underTest.findAll()).containsExactlyInAnyOrder(entity1);
    }
}