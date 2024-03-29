package com.github.saphyra.apphub.service.platform.encryption.shared_data.service;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.SharedData;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.dao.SharedDataDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SharedDataCloneService {
    private final IdGenerator idGenerator;
    private final SharedDataDao sharedDataDao;
    private final SharedDataValidator sharedDataValidator;

    public void cloneSharedData(SharedData newSharedData, DataType dataType, UUID externalId) {
        sharedDataValidator.validateForCloning(newSharedData);

        sharedDataDao.getByExternalIdAndDataType(externalId, dataType)
            .stream()
            .map(existingSharedData -> newSharedData.toBuilder()
                .sharedDataId(idGenerator.randomUuid())
                .sharedWith(existingSharedData.getSharedWith())
                .publicData(existingSharedData.getPublicData())
                .accessMode(existingSharedData.getAccessMode())
                .build())
            .forEach(sharedDataDao::save);
    }
}
