package com.github.saphyra.apphub.service.admin_panel.error_report.service.overview;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportOverview;
import com.github.saphyra.apphub.api.admin_panel.model.model.GetErrorReportsRequest;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ErrorReportOverviewQueryService {
    private final GetErrorReportsRequestValidator validator;
    private final ErrorReportDao errorReportDao;
    private final ErrorReportToOverviewConverter converter;
    private final ErrorReportOverviewSpecificationFactory specificationFactory;

    public List<ErrorReportOverview> query(GetErrorReportsRequest request) {
        validator.validate(request);

        return errorReportDao.getOverview(specificationFactory.create(request), PageRequest.of(request.getPage() - 1, request.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt")))
            .stream()
            .map(converter::convert)
            .collect(Collectors.toList());
    }
}
