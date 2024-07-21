package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.acquisition;

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
public class AcquisitionDateListProvider {
    private final AcquisitionDao acquisitionDao;

    public List<String> getDates(UUID userId) {
        return acquisitionDao.getDistinctAcquiredAtByUserId(userId)
            .stream()
            .map(LocalDate::toString)
            .collect(Collectors.toList());
    }
}
