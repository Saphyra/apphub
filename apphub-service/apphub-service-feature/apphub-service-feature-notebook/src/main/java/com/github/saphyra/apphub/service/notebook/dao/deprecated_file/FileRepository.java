package com.github.saphyra.apphub.service.notebook.dao.deprecated_file;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@Deprecated(forRemoval = true)
interface FileRepository extends CrudRepository<FileEntity, String> {
    Optional<FileEntity> findByParent(String parent);

    void deleteByUserId(String userId);

    void deleteByParent(String parent);

    int countByStoredFileId(String storedFileId);
}
