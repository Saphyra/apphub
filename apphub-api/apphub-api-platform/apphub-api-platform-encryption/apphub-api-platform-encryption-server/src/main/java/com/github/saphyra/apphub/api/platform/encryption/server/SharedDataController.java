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
    //TODO integration test
    @GetMapping(Endpoints.ENCRYPTION_INTERNAL_GET_SHARED_DATA)
    List<SharedData> getSharedData(@PathVariable("dataType") DataType dataType, @PathVariable("externalId") UUID externalId);

    //TODO integration test
    @PutMapping(Endpoints.ENCRYPTION_INTERNAL_CREATE_SHARED_DATA)
    void createSharedData(@RequestBody SharedData sharedData);

    //TODO integration test
    @PutMapping(Endpoints.ENCRYPTION_INTERNAL_CLONE_SHARED_DATA)
    void cloneSharedData(@RequestBody SharedData newSharedData, @PathVariable("dataType") DataType dataType, @PathVariable("externalId") UUID externalId);

    //TODO integration test
    @DeleteMapping(Endpoints.ENCRYPTION_INTERNAL_DELETE_SHARED_DATA_ENTITY)
    void deleteSharedDataEntity(@PathVariable("sharedDataId") UUID sharedDataId);

    //TODO integration test
    @DeleteMapping(Endpoints.ENCRYPTION_INTERNAL_DELETE_SHARED_DATA)
    void deleteSharedData(@PathVariable("dataType") DataType dataType, @PathVariable("externalId") UUID externalId);
}
