package com.github.saphyra.apphub.api.custom.villany_atesz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ToolResponse {
    private UUID toolId;
    private String brand;
    private String name;
    private Integer cost;
    private LocalDate acquiredAt;
    private LocalDate warrantyExpiresAt;
    private ToolStatus status;
    private LocalDate scrappedAt;
}
