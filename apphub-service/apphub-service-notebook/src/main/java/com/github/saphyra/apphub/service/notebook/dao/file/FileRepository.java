package com.github.saphyra.apphub.service.notebook.dao.file;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface FileRepository extends CrudRepository<FileEntity, String> {
    Optional<FileEntity> findByParent(String parent);

    void deleteByUserId(String userId);
}
