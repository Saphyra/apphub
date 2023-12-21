package com.github.saphyra.apphub.service.admin_panel.error_report.service.overview;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportOverview;
import com.github.saphyra.apphub.api.admin_panel.model.model.GetErrorReportsRequest;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDto;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ErrorReportOverviewQueryServiceTest {
    private static final Integer PAGE_SIZE = 24;
    private static final Integer PAGE = 53;

    @Mock
    private GetErrorReportsRequestValidator validator;

    @Mock
    private ErrorReportDao errorReportDao;

    @Mock
    private ErrorReportToOverviewConverter converter;

    @Mock
    private ErrorReportOverviewSpecificationFactory specificationFactory;

    @InjectMocks
    private ErrorReportOverviewQueryService underTest;

    @Mock
    private ErrorReportDto errorReport;

    @Mock
    private ErrorReportOverview errorReportOverview;

    @Mock
    private ErrorReportOverviewSpecification specification;

    @Mock
    private GetErrorReportsRequest request;

    @Test
    public void query() {
        given(specificationFactory.create(request)).willReturn(specification);
        given(request.getPageSize()).willReturn(PAGE_SIZE);
        given(request.getPage()).willReturn(PAGE);
        given(errorReportDao.getOverview(specification, PageRequest.of(PAGE - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt")))).willReturn(Arrays.asList(errorReport));
        given(converter.convert(errorReport)).willReturn(errorReportOverview);

        List<ErrorReportOverview> result = underTest.query(request);

        assertThat(result).containsExactly(errorReportOverview);
        verify(validator).validate(request);
    }
}