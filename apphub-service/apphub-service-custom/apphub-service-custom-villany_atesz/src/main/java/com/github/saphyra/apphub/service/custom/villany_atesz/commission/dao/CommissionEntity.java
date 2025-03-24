package com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "villany_atesz", name = "commission")
class CommissionEntity {
    @Id
    private String commissionId;
    private String userId;
    private String cartId;
    private String daysOfWork;
    private String hoursPerDay;
    private String departureFee;
    private String hourlyWage;
    private String extraCost;
    private String deposit;
    private String multiplier;

    @UpdateTimestamp
    private LocalDateTime lastUpdate;
}
