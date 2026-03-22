package com.github.saphyra.apphub.api.etc.admin_panel.model.model.error_report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GetErrorReportsResponse {
    private Long totalCount;
    private List<ErrorReportOverview> reports;
}
