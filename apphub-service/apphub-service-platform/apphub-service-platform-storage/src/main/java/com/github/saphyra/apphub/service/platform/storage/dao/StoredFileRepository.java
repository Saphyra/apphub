package com.github.saphyra.apphub.service.platform.storage.dao;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

interface StoredFileRepository extends CrudRepository<StoredFileEntity, String> {
    List<StoredFileEntity> getByUserId(String userid);

    void deleteByFileUploadedAndCreatedAtBefore(boolean fileUploaded, LocalDateTime expirationTime);

    @Query("SELECT new com.github.saphyra.apphub.service.platform.storage.dao.StoredFileView(s.storedFileId, s.fileUploaded) FROM StoredFileEntity s WHERE s.storage = :storage")
    List<StoredFileView> getViewsByStorage(@Param("storage") Storage storage);

    @Transactional
    void deleteByStorageAndStoredFileIdIn(Storage storage, List<String> storedFileIds);
}
