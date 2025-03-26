package com.github.saphyra.apphub.service.custom.villany_atesz.commission;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CommissionModel;
import com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao.Commission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class CommissionToResponseConverter {
    CommissionModel convert(Commission commission) {
        return CommissionModel.builder()
            .commissionId(commission.getCommissionId())
            .cartId(commission.getCartId())
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
