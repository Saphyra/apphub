package com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Deprecated(forRemoval = true)
interface DimensionRepository extends CrudRepository<DimensionEntity, String> {
    void deleteByUserId(String userId);

    List<DimensionEntity> getByExternalReference(String externalReference);
}
