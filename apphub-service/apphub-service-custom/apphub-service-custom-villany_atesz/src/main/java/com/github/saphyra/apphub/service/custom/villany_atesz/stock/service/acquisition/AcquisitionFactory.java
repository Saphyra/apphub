package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.acquisition;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition.Acquisition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class AcquisitionFactory {
    private final IdGenerator idGenerator;

    List<Acquisition> create(UUID userId, LocalDate acquiredAt, Map<UUID, Integer> acquiredItems) {
        return acquiredItems.entrySet()
            .stream()
            .filter(uuidIntegerEntry -> uuidIntegerEntry.getValue() > 0)
            .map(uuidIntegerEntry -> create(userId, acquiredAt, uuidIntegerEntry.getKey(), uuidIntegerEntry.getValue()))
            .collect(Collectors.toList());
    }

    private Acquisition create(UUID userId, LocalDate acquiredAt, UUID stockItemId, Integer amount) {
        return Acquisition.builder()
            .acquisitionId(idGenerator.randomUuid())
            .userId(userId)
            .acquiredAt(acquiredAt)
            .stockItemId(stockItemId)
            .amount(amount)
            .build();
    }
}
