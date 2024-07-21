package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AcquisitionDao extends AbstractDao<AcquisitionEntity, Acquisition, String, AcquisitionRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    AcquisitionDao(AcquisitionConverter converter, AcquisitionRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public List<LocalDate> getDistinctAcquiredAtByUserId(UUID userId) {
        return repository.getDistinctAcquiredAtByUserId(uuidConverter.convertDomain(userId))
            .stream()
            .map(LocalDate::parse)
            .collect(Collectors.toList());
    }

    public List<Acquisition> getByAcquiredAtAndUserId(LocalDate acquiredAt, UUID userId) {
        return converter.convertEntity(repository.getByAcquiredAtAndUserId(acquiredAt.toString(), uuidConverter.convertDomain(userId)));
    }

    public void deleteByStockItemIdAndUserId(UUID stockItemId, UUID userId) {
        repository.deleteByStockItemIdAndUserId(uuidConverter.convertDomain(stockItemId), uuidConverter.convertDomain(userId));
    }
}
