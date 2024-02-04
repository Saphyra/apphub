package com.github.saphyra.apphub.service.notebook.dao.pin.mapping;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PinMappingDao extends AbstractDao<PinMappingEntity, PinMapping, String, PinMappingRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public PinMappingDao(PinMappingConverter converter, PinMappingRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public List<PinMapping> getByPinGroupId(UUID pinGroupId) {
        return converter.convertEntity(repository.getByPinGroupId(uuidConverter.convertDomain(pinGroupId)));
    }

    public void deleteByPinGroupId(UUID pinGroupId) {
        repository.deleteByPinGroupId(uuidConverter.convertDomain(pinGroupId));
    }

    public Optional<PinMapping> findByPinGroupIdAndListItemId(UUID pinGroupId, UUID listItemId) {
        return converter.convertEntity(repository.findByPinGroupIdAndListItemId(
            uuidConverter.convertDomain(pinGroupId),
            uuidConverter.convertDomain(listItemId)
        ));
    }

    public PinMapping findByPinGroupIdAndListItemIdValidated(UUID pinGroupId, UUID listItemId) {
        return findByPinGroupIdAndListItemId(pinGroupId, listItemId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "PinMapping not found with pinGroupId " + pinGroupId + " and listItemId " + listItemId));
    }

    public void deleteByListItemId(UUID listItemId) {
        repository.deleteByListItemId(uuidConverter.convertDomain(listItemId));
    }
}
