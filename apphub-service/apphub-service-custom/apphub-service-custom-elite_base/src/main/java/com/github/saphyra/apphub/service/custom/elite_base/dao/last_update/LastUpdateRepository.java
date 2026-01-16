package com.github.saphyra.apphub.service.custom.elite_base.dao.last_update;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

//TODO unit test
interface LastUpdateRepository extends CrudRepository<LastUpdateEntity, LastUpdateId> {
    @Query("SELECT e FROM LastUpdateEntity e WHERE e.id.type = :itemType AND e.id.externalReference IN(:externalReferences)")
    List<LastUpdateEntity> getByItemTypeAndExternalReferences(@Param("itemType") ItemType itemType, @Param("externalReferences") List<String> externalReferences);
}
