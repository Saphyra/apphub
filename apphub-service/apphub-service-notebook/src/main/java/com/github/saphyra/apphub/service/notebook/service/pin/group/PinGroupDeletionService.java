package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.service.notebook.dao.pin.group.PinGroupDao;
import com.github.saphyra.apphub.service.notebook.dao.pin.mapping.PinMappingDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class PinGroupDeletionService {
    private final PinGroupDao pinGroupDao;
    private final PinMappingDao pinMappingDao;

    @Transactional
    public void delete(UUID pinGroupId) {
        pinGroupDao.findByIdValidated(pinGroupId); //Validate own

        pinGroupDao.deleteById(pinGroupId);
        pinMappingDao.deleteByPinGroupId(pinGroupId);
    }
}
