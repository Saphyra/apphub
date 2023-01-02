package com.github.saphyra.apphub.service.platform.encryption.shared_data.service;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.SharedData;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.dao.SharedDataDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SharedDataAccessService {
    private final SharedDataDao sharedDataDao;

    public boolean hasAccess(UUID userId, AccessMode accessMode, UUID externalId, DataType dataType) {
        List<SharedData> sharedDataList = sharedDataDao.getByExternalIdAndDataTypeAndAccessMode(externalId, dataType, accessMode);

        boolean isPublic = sharedDataList.stream()
            .anyMatch(SharedData::getPublicData);

        return isPublic || userHasAccess(userId, sharedDataList);
    }

    private boolean userHasAccess(UUID userId, List<SharedData> sharedDataList) {
        return sharedDataList.stream()
            .anyMatch(sharedData -> sharedData.getSharedWith().equals(userId));
    }
}
