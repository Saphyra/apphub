package com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class Commission {
    private final UUID commissionId;
    private final UUID userId;
    private UUID cartId;
    private Integer daysOfWork;
    private Integer hoursPerDay;
    private Integer departureFee;
    private Integer hourlyWage;
    private Integer extraCost;
    private Integer deposit;
    private Double margin;
    private LocalDateTime lastUpdate;
}
