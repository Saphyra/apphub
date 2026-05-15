package com.github.saphyra.apphub.service.platform.storage.dao.deprecated;

import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.Storage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@Deprecated(forRemoval = true)
interface DeprecatedStoredFileRepository extends CrudRepository<DeprecatedStoredFileEntity, String> {
    List<DeprecatedStoredFileEntity> getByUserId(String userid);

    void deleteByFileUploadedAndCreatedAtBefore(boolean fileUploaded, LocalDateTime expirationTime);

    @Query("SELECT new com.github.saphyra.apphub.service.platform.storage.dao.deprecated.StoredFileView(s.storedFileId, s.userId, s.fileUploaded) FROM DeprecatedStoredFileEntity s WHERE s.storage = :storage")
    List<StoredFileView> getViewsByStorage(@Param("storage") Storage storage);

    @Transactional
    void deleteByStorageAndStoredFileIdIn(Storage storage, List<String> storedFileIds);
}
