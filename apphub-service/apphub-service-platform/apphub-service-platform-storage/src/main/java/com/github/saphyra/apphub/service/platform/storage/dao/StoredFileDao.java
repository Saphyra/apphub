package com.github.saphyra.apphub.service.platform.storage.dao;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
//TODO unit test
public class StoredFileDao extends AbstractDao<StoredFileEntity, StoredFile, String, StoredFileRepository> {
    private final UuidConverter uuidConverter;

    public StoredFileDao(StoredFileConverter converter, StoredFileRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public StoredFile findByIdValidated(UUID storedFileId) {
        return findById(storedFileId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "StoredFile not found with id " + storedFileId));
    }

    private Optional<StoredFile> findById(UUID storedFileId) {
        return findById(uuidConverter.convertDomain(storedFileId));
    }
}
