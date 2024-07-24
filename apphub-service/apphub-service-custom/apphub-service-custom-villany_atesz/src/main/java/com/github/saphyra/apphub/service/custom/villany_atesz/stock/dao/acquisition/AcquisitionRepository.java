package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface AcquisitionRepository extends CrudRepository<AcquisitionEntity, String> {
    void deleteByUserId(String userId);

    @Query("SELECT distinct a.acquiredAt a FROM AcquisitionEntity a WHERE a.userId = :userId")
    List<String> getDistinctAcquiredAtByUserId(@Param("userId") String userId);

    List<AcquisitionEntity> getByAcquiredAtAndUserId(String acquiredAt, String userId);

    void deleteByStockItemIdAndUserId(String stockItemId, String userId);
}
