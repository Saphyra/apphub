package com.github.saphyra.apphub.api.platform.encryption.server;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.SharedData;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

public interface SharedDataController {
    /**
     * Checking who the given data is shared with
     */
    @GetMapping(Endpoints.ENCRYPTION_INTERNAL_GET_SHARED_DATA)
    List<SharedData> getSharedData(@PathVariable("dataType") DataType dataType, @PathVariable("externalId") UUID externalId);

    /**
     * Sharing a data with a specific user
     */
    @PutMapping(Endpoints.ENCRYPTION_INTERNAL_CREATE_SHARED_DATA)
    void createSharedData(@RequestBody SharedData sharedData);

    /**
     * Copies all the settings from records of the given entity to a new entity
     *
     * @param newSharedData skeleton to store the cloned data
     * @param dataType      dataType of the source
     * @param externalId    externalId of the source
     */
    @PutMapping(Endpoints.ENCRYPTION_INTERNAL_CLONE_SHARED_DATA)
    void cloneSharedData(@RequestBody SharedData newSharedData, @PathVariable("dataType") DataType dataType, @PathVariable("externalId") UUID externalId);

    /**
     * User no longer wants to share data with someone
     */
    @DeleteMapping(Endpoints.ENCRYPTION_INTERNAL_DELETE_SHARED_DATA_ENTITY)
    void deleteSharedDataEntity(@PathVariable("sharedDataId") UUID sharedDataId);

    /**
     * Data was deleted. All entities should be removed from the database
     */
    @DeleteMapping(Endpoints.ENCRYPTION_INTERNAL_DELETE_SHARED_DATA)
    void deleteSharedData(@PathVariable("dataType") DataType dataType, @PathVariable("externalId") UUID externalId);
}
