package com.github.saphyra.apphub.service.custom.villany_atesz.commission;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CommissionRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao.CommissionDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CommissionRequestValidator {
    private final CommissionDao commissionDao;

    public void validate(CommissionRequest request) {
        if (nonNull(request.getCommissionId())) {
            commissionDao.findByIdValidated(request.getCommissionId());
        }
        ValidationUtil.notNull(request.getDaysOfWork(), "daysOfWork");
        ValidationUtil.notNull(request.getHoursPerDay(), "hoursPerDay");
        ValidationUtil.notNull(request.getDepartureFee(), "departureFee");
        ValidationUtil.notNull(request.getHourlyWage(), "hourlyWage");
        ValidationUtil.notNull(request.getExtraCost(), "extraCost");
        ValidationUtil.notNull(request.getDeposit(), "deposit");
        ValidationUtil.notNull(request.getMargin(), "multiplier");
    }
}
