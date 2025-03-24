package com.github.saphyra.apphub.service.custom.villany_atesz.commission;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CommissionResponse;
import com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao.Commission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CommissionToResponseConverter {
    private final CommissionCartQueryService commissionCartQueryService;

    CommissionResponse convert(Commission commission) {
        return CommissionResponse.builder()
            .commissionId(commission.getCommissionId())
            .cart(Optional.ofNullable(commission.getCartId()).flatMap(commissionCartQueryService::getCart).orElse(null))
            .daysOfWork(commission.getDaysOfWork())
            .hoursPerDay(commission.getHoursPerDay())
            .departureFee(commission.getDepartureFee())
            .hourlyWage(commission.getHourlyWage())
            .extraCost(commission.getExtraCost())
            .deposit(commission.getDeposit())
            .margin(commission.getMargin())
            .lastUpdate(commission.getLastUpdate())
            .build();
    }
}
