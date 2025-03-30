package com.github.saphyra.apphub.service.custom.villany_atesz.commission;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CommissionModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao.Commission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class CommissionFactory {
    private final IdGenerator idGenerator;

    public Commission create(UUID userId, CommissionModel request) {
        return Commission.builder()
            .commissionId(Optional.ofNullable(request.getCommissionId()).orElseGet(idGenerator::randomUuid))
            .userId(userId)
            .cartId(request.getCartId())
            .daysOfWork(request.getDaysOfWork())
            .hoursPerDay(request.getHoursPerDay())
            .departureFee(request.getDepartureFee())
            .hourlyWage(request.getHourlyWage())
            .extraCost(request.getExtraCost())
            .deposit(request.getDeposit())
            .margin(request.getMargin())
            .build();
    }
}
