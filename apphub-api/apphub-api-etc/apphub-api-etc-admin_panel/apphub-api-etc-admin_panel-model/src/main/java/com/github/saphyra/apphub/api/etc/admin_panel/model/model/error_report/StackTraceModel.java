package com.github.saphyra.apphub.api.etc.admin_panel.model.model.error_report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StackTraceModel {
    private String fileName;
    private String className;
    private String methodName;
    private Integer lineNumber;
}
