package com.github.saphyra.apphub.service.platform.encryption.shared_data.service;

import com.github.saphyra.apphub.api.platform.encryption.model.SharedData;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.dao.SharedDataDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SharedDataCreationService {
    private final SharedDataValidator sharedDataValidator;
    private final IdGenerator idGenerator;
    private final SharedDataDao sharedDataDao;

    public void createSharedData(SharedData sharedData) {
        sharedDataValidator.validateForCreation(sharedData);

        sharedData.setSharedDataId(idGenerator.randomUuid());

        sharedDataDao.save(sharedData);
    }
}
