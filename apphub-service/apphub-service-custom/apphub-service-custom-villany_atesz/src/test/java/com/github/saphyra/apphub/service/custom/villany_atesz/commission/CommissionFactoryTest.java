package com.github.saphyra.apphub.service.custom.villany_atesz.commission;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CommissionModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao.Commission;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CommissionFactoryTest {
    private static final UUID CART_ID = UUID.randomUUID();
    private static final Integer DAYS_OF_WORK = 1;
    private static final Integer HOURS_PER_DAY = 12;
    private static final Integer DEPARTURE_FEE = 13;
    private static final Integer HOURLY_WAGE = 14;
    private static final Integer EXTRA_COST = 15;
    private static final Integer DEPOSIT = 16;
    private static final Double MARGIN = 231d;
    private static final UUID COMMISSION_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private CommissionFactory underTest;

    @Test
    void nullCommissionId() {
        CommissionModel request = CommissionModel.builder()
            .commissionId(null)
            .cartId(CART_ID)
            .daysOfWork(DAYS_OF_WORK)
            .hoursPerDay(HOURS_PER_DAY)
            .departureFee(DEPARTURE_FEE)
            .hourlyWage(HOURLY_WAGE)
            .extraCost(EXTRA_COST)
            .deposit(DEPOSIT)
            .margin(MARGIN)
            .build();

        given(idGenerator.randomUuid()).willReturn(COMMISSION_ID);

        assertThat(underTest.create(USER_ID, request))
            .returns(COMMISSION_ID, Commission::getCommissionId)
            .returns(USER_ID, Commission::getUserId)
            .returns(CART_ID, Commission::getCartId)
            .returns(DAYS_OF_WORK, Commission::getDaysOfWork)
            .returns(HOURS_PER_DAY, Commission::getHoursPerDay)
            .returns(DEPARTURE_FEE, Commission::getDepartureFee)
            .returns(HOURLY_WAGE, Commission::getHourlyWage)
            .returns(EXTRA_COST, Commission::getExtraCost)
            .returns(DEPOSIT, Commission::getDeposit)
            .returns(MARGIN, Commission::getMargin)
            .returns(null, Commission::getLastUpdate);
    }

    @Test
    void providedCommissionId() {
        CommissionModel request = CommissionModel.builder()
            .commissionId(COMMISSION_ID)
            .cartId(CART_ID)
            .daysOfWork(DAYS_OF_WORK)
            .hoursPerDay(HOURS_PER_DAY)
            .departureFee(DEPARTURE_FEE)
            .hourlyWage(HOURLY_WAGE)
            .extraCost(EXTRA_COST)
            .deposit(DEPOSIT)
            .margin(MARGIN)
            .build();

        assertThat(underTest.create(USER_ID, request))
            .returns(COMMISSION_ID, Commission::getCommissionId)
            .returns(USER_ID, Commission::getUserId)
            .returns(CART_ID, Commission::getCartId)
            .returns(DAYS_OF_WORK, Commission::getDaysOfWork)
            .returns(HOURS_PER_DAY, Commission::getHoursPerDay)
            .returns(DEPARTURE_FEE, Commission::getDepartureFee)
            .returns(HOURLY_WAGE, Commission::getHourlyWage)
            .returns(EXTRA_COST, Commission::getExtraCost)
            .returns(DEPOSIT, Commission::getDeposit)
            .returns(MARGIN, Commission::getMargin)
            .returns(null, Commission::getLastUpdate);

        then(idGenerator).shouldHaveNoInteractions();
    }
}