package com.github.saphyra.apphub.api.custom.villany_atesz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommissionModel {
    private UUID commissionId;
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
