package com.github.saphyra.apphub.api.admin_panel.model.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GetErrorReportsRequest {
    private String message;
    private Integer statusCode;
    private String startTime;
    private String endTime;
    private Integer pageSize;
    private Integer page;
    private String status;
    private String service;
}
