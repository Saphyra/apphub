package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.acquisition;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AcquisitionResponse;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition.AcquisitionDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AcquisitionQueryService {
    private final AcquisitionDao acquisitionDao;

    public List<AcquisitionResponse> getAcquisitionsOnDay(UUID userId, LocalDate acquiredAt) {
        return acquisitionDao.getByAcquiredAtAndUserId(acquiredAt, userId)
            .stream()
            .map(acquisition -> AcquisitionResponse.builder()
                .acquisitionId(acquisition.getAcquisitionId())
                .stockItemId(acquisition.getStockItemId())
                .amount(acquisition.getAmount())
                .build())
            .collect(Collectors.toList());
    }
}
