package com.github.saphyra.apphub.service.admin_panel.error_report.service;

import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReport;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDao;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MarkErrorReportServiceTest {
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private ErrorReportDao errorReportDao;

    @InjectMocks
    private MarkErrorReportService underTest;

    @Mock
    private ErrorReport errorReport;

    @Test
    public void mark() {
        given(errorReportDao.findAllById(Arrays.asList(ID))).willReturn(Arrays.asList(errorReport));

        underTest.mark(Arrays.asList(ID), ErrorReportStatus.READ.name());

        verify(errorReport).setStatus(ErrorReportStatus.READ);
        verify(errorReportDao).save(errorReport);
    }
}