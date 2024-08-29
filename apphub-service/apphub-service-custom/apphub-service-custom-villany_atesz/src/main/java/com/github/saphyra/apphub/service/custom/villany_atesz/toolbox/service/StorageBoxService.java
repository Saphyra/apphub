package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box.StorageBox;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box.StorageBoxDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageBoxService {
    private final StorageBoxDao storageBoxDao;

    public void edit(UUID storageBoxId, String name) {
        ValidationUtil.notBlank(name, "name");

        StorageBox storageBox = storageBoxDao.findByIdValidated(storageBoxId);

        storageBox.setName(name);

        storageBoxDao.save(storageBox);
    }

    @Transactional
    public void delete(UUID userId, UUID storageBoxId) {
        storageBoxDao.deleteByUserIdAndStorageBoxId(userId, storageBoxId);
    }
}
