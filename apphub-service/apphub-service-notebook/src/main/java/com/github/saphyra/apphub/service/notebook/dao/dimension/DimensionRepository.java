package com.github.saphyra.apphub.service.notebook.dao.dimension;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface DimensionRepository extends CrudRepository<DimensionEntity, String> {
    void deleteByUserId(String userId);

    List<DimensionEntity> getByExternalReference(String externalReference);
}
