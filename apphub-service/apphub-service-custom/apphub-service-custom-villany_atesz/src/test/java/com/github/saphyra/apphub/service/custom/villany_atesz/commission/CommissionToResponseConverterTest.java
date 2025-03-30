package com.github.saphyra.apphub.service.custom.villany_atesz.commission;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CommissionModel;
import com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao.Commission;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CommissionToResponseConverterTest {
    private static final UUID COMMISSION_ID = UUID.randomUUID();
    private static final UUID CART_ID = UUID.randomUUID();
    private static final Integer DAYS_OF_WORK = 1;
    private static final Integer HOURS_PER_DAY = 12;
    private static final Integer DEPARTURE_FEE = 13;
    private static final Integer HOURLY_WAGE = 14;
    private static final Integer EXTRA_COST = 15;
    private static final Integer DEPOSIT = 16;
    private static final Double MARGIN = 17d;
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();

    @InjectMocks
    private CommissionToResponseConverter underTest;

    @Test
    void convert() {
        Commission commission = Commission.builder()
            .commissionId(COMMISSION_ID)
            .cartId(CART_ID)
            .daysOfWork(DAYS_OF_WORK)
            .hoursPerDay(HOURS_PER_DAY)
            .departureFee(DEPARTURE_FEE)
            .hourlyWage(HOURLY_WAGE)
            .extraCost(EXTRA_COST)
            .deposit(DEPOSIT)
            .margin(MARGIN)
            .lastUpdate(LAST_UPDATE)
            .build();

        assertThat(underTest.convert(commission))
            .returns(COMMISSION_ID, CommissionModel::getCommissionId)
            .returns(CART_ID, CommissionModel::getCartId)
            .returns(DAYS_OF_WORK, CommissionModel::getDaysOfWork)
            .returns(HOURS_PER_DAY, CommissionModel::getHoursPerDay)
            .returns(DEPARTURE_FEE, CommissionModel::getDepartureFee)
            .returns(HOURLY_WAGE, CommissionModel::getHourlyWage)
            .returns(EXTRA_COST, CommissionModel::getExtraCost)
            .returns(DEPOSIT, CommissionModel::getDeposit)
            .returns(MARGIN, CommissionModel::getMargin)
            .returns(LAST_UPDATE, CommissionModel::getLastUpdate);
    }
}