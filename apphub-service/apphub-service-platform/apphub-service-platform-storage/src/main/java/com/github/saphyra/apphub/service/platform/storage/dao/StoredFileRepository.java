package com.github.saphyra.apphub.service.platform.storage.dao;

import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

interface StoredFileRepository extends CrudRepository<StoredFileEntity, String> {
    List<StoredFileEntity> getByUserId(String userid);

    void deleteByFileUploadedAndCreatedAtBefore(boolean fileUploaded, LocalDateTime expirationTime);
}
