package com.github.saphyra.apphub.service.platform.storage.dao;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class StoredFileDao extends AbstractDao<StoredFileEntity, StoredFile, String, StoredFileRepository> {
    private final UuidConverter uuidConverter;

    public StoredFileDao(StoredFileConverter converter, StoredFileRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public Optional<StoredFile> findById(UUID storedFileId) {
        return findById(uuidConverter.convertDomain(storedFileId));
    }

    public StoredFile findByIdValidated(UUID storedFileId) {
        return findById(storedFileId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "StoredFile not found with id " + storedFileId));
    }

    public List<StoredFile> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    @Transactional
    public void deleteExpired(LocalDateTime expirationTime) {
        repository.deleteByFileUploadedAndCreatedAtBefore(false, expirationTime);
    }
}
