package com.github.saphyra.apphub.service.notebook.dao.pin.mapping;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

interface PinMappingRepository extends CrudRepository<PinMappingEntity, String> {
    void deleteByUserId(String userId);

    List<PinMappingEntity> getByPinGroupId(String pinGroupId);

    void deleteByPinGroupId(String pinGroupId);

    Optional<PinMappingEntity> findByPinGroupIdAndListItemId(String pinGroupId, String listItemId);

    void deleteByListItemId(String listItemId);
}
