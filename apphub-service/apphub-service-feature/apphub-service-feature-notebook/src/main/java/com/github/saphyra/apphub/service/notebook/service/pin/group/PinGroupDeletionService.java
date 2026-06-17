package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PinGroupDeletionService {
    private final PinGroupDao pinGroupDao;

    @Transactional
    public void delete(UUID userId, UUID pinGroupId) {
        pinGroupDao.delete(userId, pinGroupId);
    }
}
