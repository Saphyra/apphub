package com.github.saphyra.apphub.service.platform.encryption.shared_data;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.SharedData;
import com.github.saphyra.apphub.api.platform.encryption.server.SharedDataController;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.dao.SharedDataDao;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.service.SharedDataCloneService;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.service.SharedDataCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SharedDataControllerImpl implements SharedDataController {
    private final SharedDataCreationService sharedDataCreationService;
    private final SharedDataCloneService sharedDataCloneService;
    private final SharedDataDao sharedDataDao;

    @Override
    public List<SharedData> getSharedData(DataType dataType, UUID externalId) {
        log.info("Querying SharedData for {} - {}", dataType, externalId);
        return sharedDataDao.getByExternalIdAndDataType(externalId, dataType);
    }

    @Override
    public void createSharedData(SharedData sharedData) {
        log.info("Creating {}", sharedData);
        sharedDataCreationService.createSharedData(sharedData);
    }

    @Override
    public void cloneSharedData(SharedData newSharedData, DataType dataType, UUID externalId) {
        log.info("Cloning SharedData of {} - {} to {} - {}", dataType, externalId, newSharedData.getDataType(), newSharedData.getExternalId());
        sharedDataCloneService.cloneSharedData(newSharedData, dataType, externalId);
    }

    @Override
    public void deleteSharedDataEntity(UUID sharedDataId) {
        log.info("Deleting SharedData {}", sharedDataId);
        sharedDataDao.deleteById(sharedDataId);
    }

    @Override
    @Transactional
    public void deleteSharedData(DataType dataType, UUID externalId) {
        log.info("Deleting SharedData for {} - {}", dataType, externalId);
        sharedDataDao.deleteByExternalIdAndDataType(externalId, dataType);
    }
}
