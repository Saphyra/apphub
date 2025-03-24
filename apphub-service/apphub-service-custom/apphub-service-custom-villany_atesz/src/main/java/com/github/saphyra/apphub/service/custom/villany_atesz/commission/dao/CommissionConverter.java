package com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.DoubleEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CommissionConverter extends ConverterBase<CommissionEntity, Commission> {
    private static final String COLUMN_DAYS_OF_WORK = "days-of-work";
    private static final String COLUMN_HOURS_PER_DAY = "hours-per-day";
    private static final String COLUMN_DEPARTURE_FEE = "departure-fee";
    private static final String COLUMN_HOURLY_WAGE = "hourly-wage";
    private static final String COLUMN_EXTRA_COST = "extra-cost";
    private static final String COLUMN_DEPOSIT = "deposit";
    private static final String COLUMN_MULTIPLIER = "multiplier";

    private final UuidConverter uuidConverter;
    private final IntegerEncryptor integerEncryptor;
    private final DoubleEncryptor doubleEncryptor;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected CommissionEntity processDomainConversion(Commission domain) {
        String uidFromAccessToken = accessTokenProvider.getUserIdAsString();
        String commissionId = uuidConverter.convertDomain(domain.getCommissionId());

        return CommissionEntity.builder()
            .commissionId(commissionId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .cartId(uuidConverter.convertDomain(domain.getCartId()))
            .daysOfWork(integerEncryptor.encrypt(domain.getDaysOfWork(), uidFromAccessToken, commissionId, COLUMN_DAYS_OF_WORK))
            .hoursPerDay(integerEncryptor.encrypt(domain.getHoursPerDay(), uidFromAccessToken, commissionId, COLUMN_HOURS_PER_DAY))
            .departureFee(integerEncryptor.encrypt(domain.getDepartureFee(), uidFromAccessToken, commissionId, COLUMN_DEPARTURE_FEE))
            .hourlyWage(integerEncryptor.encrypt(domain.getHourlyWage(), uidFromAccessToken, commissionId, COLUMN_HOURLY_WAGE))
            .extraCost(integerEncryptor.encrypt(domain.getExtraCost(), uidFromAccessToken, commissionId, COLUMN_EXTRA_COST))
            .deposit(integerEncryptor.encrypt(domain.getDeposit(), uidFromAccessToken, commissionId, COLUMN_DEPOSIT))
            .multiplier(doubleEncryptor.encrypt(domain.getMargin(), uidFromAccessToken, commissionId, COLUMN_MULTIPLIER))
            .build();
    }

    @Override
    protected Commission processEntityConversion(CommissionEntity entity) {
        String uidFromAccessToken = accessTokenProvider.getUserIdAsString();

        return Commission.builder()
            .commissionId(uuidConverter.convertEntity(entity.getCommissionId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .cartId(uuidConverter.convertEntity(entity.getCartId()))
            .daysOfWork(integerEncryptor.decrypt(entity.getDaysOfWork(), uidFromAccessToken, entity.getCommissionId(), COLUMN_DAYS_OF_WORK))
            .hoursPerDay(integerEncryptor.decrypt(entity.getHoursPerDay(), uidFromAccessToken, entity.getCommissionId(), COLUMN_HOURS_PER_DAY))
            .departureFee(integerEncryptor.decrypt(entity.getDepartureFee(), uidFromAccessToken, entity.getCommissionId(), COLUMN_DEPARTURE_FEE))
            .hourlyWage(integerEncryptor.decrypt(entity.getHourlyWage(), uidFromAccessToken, entity.getCommissionId(), COLUMN_HOURLY_WAGE))
            .extraCost(integerEncryptor.decrypt(entity.getExtraCost(), uidFromAccessToken, entity.getCommissionId(), COLUMN_EXTRA_COST))
            .deposit(integerEncryptor.decrypt(entity.getDeposit(), uidFromAccessToken, entity.getCommissionId(), COLUMN_DEPOSIT))
            .margin(doubleEncryptor.decrypt(entity.getMultiplier(), uidFromAccessToken, entity.getCommissionId(), COLUMN_MULTIPLIER))
            .lastUpdate(entity.getLastUpdate())
            .build();
    }
}
