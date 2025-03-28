package com.github.saphyra.apphub.service.custom.villany_atesz.commission;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CommissionModel;
import com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao.CommissionDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CommissionRequestValidatorTest {
    private static final UUID COMMISSION_ID = UUID.randomUUID();

    @Mock
    private CommissionDao commissionDao;

    @InjectMocks
    private CommissionRequestValidator underTest;

    @Test
    void nullDaysOfWork() {
        CommissionModel request = CommissionModel.builder()
            .commissionId(COMMISSION_ID)
            .daysOfWork(null)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "daysOfWork", "must not be null");

        then(commissionDao).should().findByIdValidated(COMMISSION_ID);
    }

    @Test
    void nullHoursPerDay() {
        CommissionModel request = CommissionModel.builder()
            .commissionId(null)
            .daysOfWork(12)
            .hoursPerDay(null)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "hoursPerDay", "must not be null");

        then(commissionDao).shouldHaveNoInteractions();
    }

    @Test
    void nullDepartureFee() {
        CommissionModel request = CommissionModel.builder()
            .commissionId(COMMISSION_ID)
            .daysOfWork(12)
            .hoursPerDay(5)
            .departureFee(null)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "departureFee", "must not be null");

        then(commissionDao).should().findByIdValidated(COMMISSION_ID);
    }

    @Test
    void nullHourlyWage() {
        CommissionModel request = CommissionModel.builder()
            .commissionId(COMMISSION_ID)
            .daysOfWork(12)
            .hoursPerDay(5)
            .departureFee(576)
            .hourlyWage(null)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "hourlyWage", "must not be null");

        then(commissionDao).should().findByIdValidated(COMMISSION_ID);
    }

    @Test
    void nullExtraCost() {
        CommissionModel request = CommissionModel.builder()
            .commissionId(COMMISSION_ID)
            .daysOfWork(12)
            .hoursPerDay(5)
            .departureFee(576)
            .hourlyWage(7)
            .extraCost(null)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "extraCost", "must not be null");

        then(commissionDao).should().findByIdValidated(COMMISSION_ID);
    }

    @Test
    void nullDeposit() {
        CommissionModel request = CommissionModel.builder()
            .commissionId(COMMISSION_ID)
            .daysOfWork(12)
            .hoursPerDay(5)
            .departureFee(576)
            .hourlyWage(7)
            .extraCost(87)
            .deposit(null)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "deposit", "must not be null");

        then(commissionDao).should().findByIdValidated(COMMISSION_ID);
    }

    @Test
    void nullMargin() {
        CommissionModel request = CommissionModel.builder()
            .commissionId(COMMISSION_ID)
            .daysOfWork(12)
            .hoursPerDay(5)
            .departureFee(576)
            .hourlyWage(7)
            .extraCost(87)
            .deposit(6)
            .margin(null)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "margin", "must not be null");

        then(commissionDao).should().findByIdValidated(COMMISSION_ID);
    }

    @Test
    void valid() {
        CommissionModel request = CommissionModel.builder()
            .commissionId(COMMISSION_ID)
            .daysOfWork(12)
            .hoursPerDay(5)
            .departureFee(576)
            .hourlyWage(7)
            .extraCost(87)
            .deposit(6)
            .margin(3d)
            .build();

        underTest.validate(request);

        then(commissionDao).should().findByIdValidated(COMMISSION_ID);
    }
}