package com.github.saphyra.apphub.service.notebook.dao.deprecated_content;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

@Deprecated(forRemoval = true)
interface DeprecatedContentRepository extends CrudRepository<ContentEntity, String> {
    void deleteByParent(String parent);

    Optional<ContentEntity> findByParent(String parent);

    void deleteByUserId(String userId);

    List<ContentEntity> getByUserId(String userId);
}
