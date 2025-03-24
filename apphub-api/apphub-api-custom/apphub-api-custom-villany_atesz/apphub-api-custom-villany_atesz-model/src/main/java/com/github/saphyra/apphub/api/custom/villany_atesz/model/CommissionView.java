package com.github.saphyra.apphub.api.custom.villany_atesz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class CommissionView {
    private UUID commissionId;
    private String contactName;
    private LocalDateTime lastUpdate;
}
