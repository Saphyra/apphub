package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ColumnDataServiceFetcherTest {
    private ColumnDataServiceFetcher underTest;

    @Mock
    private ColumnDataService columnDataService;

    @BeforeEach
    void setUp() {
        underTest = new ColumnDataServiceFetcher(List.of(columnDataService));
    }

    @Test
    void columnDataServiceNotFound() {
        given(columnDataService.canProcess(ColumnType.TEXT)).willReturn(false);

        Throwable ex = catchThrowable(() -> underTest.findColumnDataService(ColumnType.TEXT));

        ExceptionValidator.validateReportedException(ex, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR);
    }

    @Test
    void findColumnDataService() {
        given(columnDataService.canProcess(ColumnType.TEXT)).willReturn(true);

        assertThat(underTest.findColumnDataService(ColumnType.TEXT)).isEqualTo(columnDataService);
    }
}