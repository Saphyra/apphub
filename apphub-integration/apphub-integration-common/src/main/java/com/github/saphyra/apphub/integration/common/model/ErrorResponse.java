package com.github.saphyra.apphub.integration.common.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
public class ErrorResponse {
    private String errorCode;
    private String localizedMessage;
    private Map<String, String> params;
}
