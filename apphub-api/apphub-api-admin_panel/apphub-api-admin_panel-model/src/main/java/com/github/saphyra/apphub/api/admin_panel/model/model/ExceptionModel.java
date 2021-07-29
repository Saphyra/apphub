package com.github.saphyra.apphub.api.admin_panel.model.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ExceptionModel {
    private String message;
    private String type;
    private String thread;
    private List<StackTraceModel> stackTrace;
    private ExceptionModel cause;
}
