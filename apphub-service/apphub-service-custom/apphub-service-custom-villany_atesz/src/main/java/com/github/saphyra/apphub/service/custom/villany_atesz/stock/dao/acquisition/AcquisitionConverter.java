package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
class AcquisitionConverter extends ConverterBase<AcquisitionEntity, Acquisition> {
    static final String COLUMN_AMOUNT = "amount";

    private final UuidConverter uuidConverter;
    private final AccessTokenProvider accessTokenProvider;
    private final IntegerEncryptor integerEncryptor;

    @Override
    protected AcquisitionEntity processDomainConversion(Acquisition domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        String acquisitionId = uuidConverter.convertDomain(domain.getAcquisitionId());

        return AcquisitionEntity.builder()
            .acquisitionId(acquisitionId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .acquiredAt(domain.getAcquiredAt().toString())
            .stockItemId(uuidConverter.convertDomain(domain.getStockItemId()))
            .amount(integerEncryptor.encrypt(domain.getAmount(), userId, acquisitionId, COLUMN_AMOUNT))
            .build();
    }

    @Override
    protected Acquisition processEntityConversion(AcquisitionEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();

        return Acquisition.builder()
            .acquisitionId(uuidConverter.convertEntity(entity.getAcquisitionId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .acquiredAt(LocalDate.parse(entity.getAcquiredAt()))
            .stockItemId(uuidConverter.convertEntity(entity.getStockItemId()))
            .amount(integerEncryptor.decrypt(entity.getAmount(), userId, entity.getAcquisitionId(), COLUMN_AMOUNT))
            .build();
    }
}
