package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.acquisition;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AcquisitionRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.AddToStockRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition.Acquisition;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition.AcquisitionDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AcquisitionService {
    private final AcquisitionDao acquisitionDao;
    private final AcquisitionFactory acquisitionFactory;

    public void createAcquisitions(UUID userId, AcquisitionRequest request) {
        Map<UUID, Integer> acquiredItems = request.getItems()
            .stream()
            .collect(Collectors.groupingBy(AddToStockRequest::getStockItemId))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, uuidListEntry -> uuidListEntry.getValue().stream().mapToInt(value -> value.getInStorage() + value.getInCar()).sum()));

        List<Acquisition> acquisitions = acquisitionFactory.create(userId, request.getAcquiredAt(), acquiredItems);

        acquisitionDao.saveAll(acquisitions);
    }
}
