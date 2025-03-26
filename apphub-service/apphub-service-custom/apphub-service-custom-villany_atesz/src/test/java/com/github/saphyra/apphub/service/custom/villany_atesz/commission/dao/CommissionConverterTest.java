package com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.DoubleEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao.CommissionConverter.COLUMN_DAYS_OF_WORK;
import static com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao.CommissionConverter.COLUMN_DEPARTURE_FEE;
import static com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao.CommissionConverter.COLUMN_DEPOSIT;
import static com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao.CommissionConverter.COLUMN_EXTRA_COST;
import static com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao.CommissionConverter.COLUMN_HOURLY_WAGE;
import static com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao.CommissionConverter.COLUMN_HOURS_PER_DAY;
import static com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao.CommissionConverter.COLUMN_MARGIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommissionConverterTest {
    private static final UUID COMMISSION_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CART_ID = UUID.randomUUID();
    private static final Integer DAYS_OF_WORK = 1;
    private static final Integer HOURS_PER_DAY = 2;
    private static final Integer DEPARTURE_FEE = 3;
    private static final Integer HOURLY_WAGE = 4;
    private static final Integer EXTRA_COST = 5;
    private static final Integer DEPOSIT = 6;
    private static final Double MARGIN = 7d;
    private static final String USER_ID_FROM_ACCESS_TOKEN = "user-id-from-access-token";
    private static final String COMMISSION_ID_STRING = "commission-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String CART_ID_STRING = "cart-id";
    private static final String ENCRYPTED_DAYS_OF_WORK = "days-of-work";
    private static final String ENCRYPTED_HOURS_PER_DAY = "hours-per-day";
    private static final String ENCRYPTED_DEPARTURE_FEE = "departure-fee";
    private static final String ENCRYPTED_HOURLY_WAGE = "hourly-wage";
    private static final String ENCRYPTED_EXTRA_COST = "extra-cost";
    private static final String ENCRYPTED_DEPOSIT = "deposit";
    private static final String ENCRYPTED_MARGIN = "margin";
    private static final LocalDateTime LAST_UPDATE = LocalDateTime.now();

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private IntegerEncryptor integerEncryptor;

    @Mock
    private DoubleEncryptor doubleEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private CommissionConverter underTest;

    @Test
    void convertDomain() {
        Commission domain = Commission.builder()
            .commissionId(COMMISSION_ID)
            .userId(USER_ID)
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

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(uuidConverter.convertDomain(COMMISSION_ID)).willReturn(COMMISSION_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(CART_ID)).willReturn(CART_ID_STRING);

        given(integerEncryptor.encrypt(DAYS_OF_WORK, USER_ID_FROM_ACCESS_TOKEN, COMMISSION_ID_STRING, COLUMN_DAYS_OF_WORK)).willReturn(ENCRYPTED_DAYS_OF_WORK);
        given(integerEncryptor.encrypt(HOURS_PER_DAY, USER_ID_FROM_ACCESS_TOKEN, COMMISSION_ID_STRING, COLUMN_HOURS_PER_DAY)).willReturn(ENCRYPTED_HOURS_PER_DAY);
        given(integerEncryptor.encrypt(DEPARTURE_FEE, USER_ID_FROM_ACCESS_TOKEN, COMMISSION_ID_STRING, COLUMN_DEPARTURE_FEE)).willReturn(ENCRYPTED_DEPARTURE_FEE);
        given(integerEncryptor.encrypt(HOURLY_WAGE, USER_ID_FROM_ACCESS_TOKEN, COMMISSION_ID_STRING, COLUMN_HOURLY_WAGE)).willReturn(ENCRYPTED_HOURLY_WAGE);
        given(integerEncryptor.encrypt(EXTRA_COST, USER_ID_FROM_ACCESS_TOKEN, COMMISSION_ID_STRING, COLUMN_EXTRA_COST)).willReturn(ENCRYPTED_EXTRA_COST);
        given(integerEncryptor.encrypt(DEPOSIT, USER_ID_FROM_ACCESS_TOKEN, COMMISSION_ID_STRING, COLUMN_DEPOSIT)).willReturn(ENCRYPTED_DEPOSIT);
        given(doubleEncryptor.encrypt(MARGIN, USER_ID_FROM_ACCESS_TOKEN, COMMISSION_ID_STRING, COLUMN_MARGIN)).willReturn(ENCRYPTED_MARGIN);

        assertThat(underTest.convertDomain(domain))
            .returns(COMMISSION_ID_STRING, CommissionEntity::getCommissionId)
            .returns(USER_ID_STRING, CommissionEntity::getUserId)
            .returns(CART_ID_STRING, CommissionEntity::getCartId)
            .returns(ENCRYPTED_DAYS_OF_WORK, CommissionEntity::getDaysOfWork)
            .returns(ENCRYPTED_HOURS_PER_DAY, CommissionEntity::getHoursPerDay)
            .returns(ENCRYPTED_DEPARTURE_FEE, CommissionEntity::getDepartureFee)
            .returns(ENCRYPTED_HOURLY_WAGE, CommissionEntity::getHourlyWage)
            .returns(ENCRYPTED_EXTRA_COST, CommissionEntity::getExtraCost)
            .returns(ENCRYPTED_DEPOSIT, CommissionEntity::getDeposit)
            .returns(ENCRYPTED_MARGIN, CommissionEntity::getMargin)
            .returns(null, CommissionEntity::getLastUpdate);
    }

    @Test
    void convertEntity() {
        CommissionEntity entity = CommissionEntity.builder()
            .commissionId(COMMISSION_ID_STRING)
            .userId(USER_ID_STRING)
            .cartId(CART_ID_STRING)
            .daysOfWork(ENCRYPTED_DAYS_OF_WORK)
            .hoursPerDay(ENCRYPTED_HOURS_PER_DAY)
            .departureFee(ENCRYPTED_DEPARTURE_FEE)
            .hourlyWage(ENCRYPTED_HOURLY_WAGE)
            .extraCost(ENCRYPTED_EXTRA_COST)
            .deposit(ENCRYPTED_DEPOSIT)
            .margin(ENCRYPTED_MARGIN)
            .lastUpdate(LAST_UPDATE)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(uuidConverter.convertEntity(COMMISSION_ID_STRING)).willReturn(COMMISSION_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(CART_ID_STRING)).willReturn(CART_ID);

        given(integerEncryptor.decrypt(ENCRYPTED_DAYS_OF_WORK, USER_ID_FROM_ACCESS_TOKEN, COMMISSION_ID_STRING, COLUMN_DAYS_OF_WORK)).willReturn(DAYS_OF_WORK);
        given(integerEncryptor.decrypt(ENCRYPTED_HOURS_PER_DAY, USER_ID_FROM_ACCESS_TOKEN, COMMISSION_ID_STRING, COLUMN_HOURS_PER_DAY)).willReturn(HOURS_PER_DAY);
        given(integerEncryptor.decrypt(ENCRYPTED_DEPARTURE_FEE, USER_ID_FROM_ACCESS_TOKEN, COMMISSION_ID_STRING, COLUMN_DEPARTURE_FEE)).willReturn(DEPARTURE_FEE);
        given(integerEncryptor.decrypt(ENCRYPTED_HOURLY_WAGE, USER_ID_FROM_ACCESS_TOKEN, COMMISSION_ID_STRING, COLUMN_HOURLY_WAGE)).willReturn(HOURLY_WAGE);
        given(integerEncryptor.decrypt(ENCRYPTED_EXTRA_COST, USER_ID_FROM_ACCESS_TOKEN, COMMISSION_ID_STRING, COLUMN_EXTRA_COST)).willReturn(EXTRA_COST);
        given(integerEncryptor.decrypt(ENCRYPTED_DEPOSIT, USER_ID_FROM_ACCESS_TOKEN, COMMISSION_ID_STRING, COLUMN_DEPOSIT)).willReturn(DEPOSIT);
        given(doubleEncryptor.decrypt(ENCRYPTED_MARGIN, USER_ID_FROM_ACCESS_TOKEN, COMMISSION_ID_STRING, COLUMN_MARGIN)).willReturn(MARGIN);

        assertThat(underTest.convertEntity(entity))
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
            .returns(LAST_UPDATE, Commission::getLastUpdate);
    }
}