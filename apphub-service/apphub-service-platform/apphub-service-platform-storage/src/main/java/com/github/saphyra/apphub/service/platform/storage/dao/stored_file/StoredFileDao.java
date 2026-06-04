package com.github.saphyra.apphub.service.platform.storage.dao.stored_file;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StoredFileDao {
    private final StoredFileRepository repository;
    private final StoredFileConverter converter;
    private final UuidConverter uuidConverter;

    public List<StoredFile> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public StoredFile findByIdValidated(UUID userId, UUID storedFileId) {
        return findById(userId, storedFileId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "StoredFile not found with id " + storedFileId + " for userId " + userId));
    }

    public Optional<StoredFile> findById(UUID userId, UUID storedFileId) {
        return converter.convertEntity(repository.findById(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(storedFileId)));
    }

    public void delete(StoredFile storedFile) {
        repository.delete(uuidConverter.convertDomain(storedFile.getUserId()), uuidConverter.convertDomain(storedFile.getStoredFileId()));
    }

    public void save(StoredFile storedFile) {
        repository.save(converter.convertDomain(storedFile));
    }

    @Deprecated(forRemoval = true)
    public List<BiWrapper<UUID, UUID>> getFtpFileIds() {
        return repository.getAllIdsWithFtpStorage()
            .stream()
            .map(bw -> new BiWrapper<>(uuidConverter.convertEntity(bw.getEntity1()), uuidConverter.convertEntity(bw.getEntity2())))
            .toList();
    }
}
